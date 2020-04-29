package org.ariia.core.api.service;

import java.io.Closeable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ariia.items.Builder;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.items.ItemStore;
import org.ariia.logging.Log;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.writer.ChannelMetaDataWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.StreamMetaDataWriter;
import org.ariia.monitors.MiniTableMonitor;
import org.ariia.monitors.SessionMonitor;
import org.ariia.monitors.SimpleSessionMonitor;
import org.ariia.monitors.TableMonitor;
import org.ariia.network.ConnectivityCheck;
import org.ariia.network.NetworkReport;
import org.ariia.network.NetworkStatus;
import org.ariia.network.UrlConnectivity;
import org.ariia.range.RangeUtil;

public class ServiceManager implements Closeable {
	
//	public static ServiceManager SegmentServiceManager(Proxy proxy) {
//		return  new ServiceManager(new SegmentClient(proxy));
//	}
//	
//	public static ServiceManager ChannelServiceManager(Proxy proxy) {
//		return new ServiceManager(new ChannelClient(proxy));
//	}
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	protected ScheduledExecutorService scheduledService;

	protected Queue<ItemMetaData> wattingList;
	protected Queue<ItemMetaData> downloadingList;
	protected Queue<ItemMetaData> completeingList;

	protected DataStore<Item> dataStore;
	protected Client client;
	protected SessionMonitor sessionMonitor;
	protected TableMonitor reportTable;
	protected ConnectivityCheck connectivity;
	

	private Runnable finishAction = ()->{};
	
	public ServiceManager(Client client) {
		this.client = client;
		this.sessionMonitor = new SimpleSessionMonitor(); 
		this.reportTable = new MiniTableMonitor(sessionMonitor);
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new UrlConnectivity(client.getProxy());

		this.initServiceList(new ItemStore());
		
	}
	
	public ServiceManager(Client client, DataStore<Item> dataStore) {
		this.client = client;
		this.sessionMonitor = new SimpleSessionMonitor(); 
		this.reportTable = new MiniTableMonitor(sessionMonitor);
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new UrlConnectivity(client.getProxy());
		this.initServiceList(dataStore);
	}
	
	public ServiceManager(Client client, TableMonitor reportTable) {
		this.client = client;
		this.reportTable = reportTable;
		this.sessionMonitor = reportTable.getSessionMonitor(); 
		this.connectivity = new UrlConnectivity(client.getProxy());

		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.initServiceList(new ItemStore());
	}
	
	public ServiceManager(Client client, SessionMonitor monitor, TableMonitor reportTable) {
		this(client, monitor, new UrlConnectivity(client.getProxy()), reportTable);
	}
	
	public ServiceManager(Client client, SessionMonitor monitor,
			ConnectivityCheck connectivity, TableMonitor reportTable) {
		this.client = client;
		this.sessionMonitor = monitor; 
		this.reportTable = reportTable;
		this.connectivity = connectivity;

		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.initServiceList(new ItemStore());
	}

	protected void initServiceList(DataStore<Item> dataStore) {
		this.dataStore			= dataStore;
		this.wattingList		= new LinkedList<>();
		this.downloadingList	= new LinkedList<>();
		this.completeingList	= new LinkedList<>();
	}
	
	
//	@Override
	public void startScheduledService() {
		scheduledService.execute(this::saveWattingItemToDisk);

//		scheduledService.scheduleWithFixedDelay(this::checkInternetConnectivity, 0, 1, TimeUnit.SECONDS);
		
		// for each 2 second
		scheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 1, 1, TimeUnit.SECONDS);
	}

	@Override
	public void close() {
		client.stopService();
		scheduledService.shutdownNow();
	}
		
	public boolean isNetworkFailer() {
		if (sessionMonitor.isDownloading()) {
			return false;
		} else {
			NetworkReport report = connectivity.networkReport();
			Log.trace(connectivity.getClass(), report.getTitle(), report.getMessage());
			if (NetworkStatus.Connected.equals(report.getNetworkStatus())) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	protected void checkdownloadList() {
		if (isNetworkFailer()) {
			Log.log(getClass(), "Check Network Connection",
					"Network Connectivity Statues: NETWORK DISCONNECTED");
			for (ItemMetaData metaData : downloadingList) {
				metaData.pause();
				downloadingList.remove(metaData);
//				removeItemEvent(item);
				wattingList.add(metaData);
				metaData.getItem().setState(ItemState.PAUSE);
				dataStore.save(metaData.getItem());
			}
		} else {
			if(downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL) {
				StringBuilder builder = new StringBuilder();
				while (downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
					ItemMetaData metaData = wattingList.poll();
					addItemEvent(metaData);
					builder.append(metaData.getItem().getFilename());
					builder.append("\tRemaining: ");
					builder.append( metaData.getItem().getRangeInfo().getRemainingLengthMB());
					builder.append('\n');
				}
				if(builder.length() != 0) {
					builder.delete(builder.length()-2, builder.length());
					Log.log(getClass(), "items added to download list", builder.toString());
				}
			}
			
			for (ItemMetaData metaData : downloadingList) {
				Item item = metaData.getItem();
				RangeUtil info = item.getRangeInfo();
				if (info.isFinish()) {
					removeItemEvent(metaData);
					Log.log(getClass(), "Download Finish: " + metaData.getItem().getFilename(),
							metaData.getItem().liteString());
					continue;
				}
				metaData.checkWhileDownloading();
				metaData.startAndCheckDownloadQueue(client, sessionMonitor);
			}
			
			if (downloadingList.isEmpty() & wattingList.isEmpty()) {
				finishAction.run();
			}
		}
		
	}
	
	
	protected void addItemEvent(ItemMetaData metaData) {
		metaData.getItem().setState(ItemState.DOWNLOADING);
		dataStore.save(metaData.getItem());
		metaData.initWaitQueue();
		metaData.checkCompleted();
		downloadingList.add(metaData);
		metaData.startAndCheckDownloadQueue(client, sessionMonitor);
		reportTable.add(metaData.getRangeMointor());
		metaData.getItem().getRangeInfo().oneCycleDataUpdate();
	}

	protected void removeItemEvent(ItemMetaData metaData) {
		metaData.systemFlush();
		metaData.getItem().setState(ItemState.COMPLETE);
		dataStore.save(metaData.getItem());
		metaData.close();
		downloadingList.remove(metaData);
		reportTable.remove(metaData.getRangeMointor());
		completeingList.add(metaData);
	}
	
	public void runSystemShutdownHook() {
		for (ItemMetaData metaData : downloadingList) {
			metaData.systemFlush();
			dataStore.save(metaData.getItem());
			metaData.close();
		}
		
		for (ItemMetaData metaData : wattingList) {
			dataStore.save(metaData.getItem());
			metaData.close();
		}
	}
	
	
	public void setFinishAction(Runnable runnable) {
		this.finishAction = runnable;
	}
	
	
	public ScheduledExecutorService getScheduledService() {
		return scheduledService;
	}

	public void setCheduledService(ScheduledExecutorService cheduledService) {
		this.scheduledService = cheduledService;
	}

	protected Queue<ItemMetaData> getWattingList() {
		return wattingList;
	}

	protected void setWattingList(Queue<ItemMetaData> wattingList) {
		this.wattingList = wattingList;
	}

	protected Queue<ItemMetaData> getDownloadingList() {
		return downloadingList;
	}

	protected void setDownloadingList(Queue<ItemMetaData> downloadingList) {
		this.downloadingList = downloadingList;
	}

	public DataStore<Item> getDataStore() {
		return dataStore;
	}
	
	public void setDataStore(DataStore<Item> dataStore) {
		this.dataStore = dataStore;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public SessionMonitor getSessionMointor() {
		return sessionMonitor;
	}

	public void setSessionMointor(SimpleSessionMonitor monitor) {
		this.sessionMonitor = monitor;
	}
	
	
	public void printReport() {
		System.out.println(reportTable.getTableReport());
	}
	
	public void printAllReport() {
		for (ItemMetaData itemMetaData : completeingList) {
			reportTable.add(itemMetaData.getRangeMointor());
		}
		System.out.println(reportTable.getTableReport());
	}

	protected void systemFlushData() {
		for (ItemMetaData metaData : downloadingList) {
			metaData.systemFlush();
			dataStore.save(metaData.getItem());
		}
	}
	



	protected void saveDownloadingItemToDisk() {
		for (ItemMetaData metaData : downloadingList) {
			dataStore.save(metaData.getItem());
		}
	}

	protected void saveWattingItemToDisk() {
		for (ItemMetaData metaData : wattingList) {
			dataStore.save(metaData.getItem());
		}
	}
	
	public String download(String url) {
		return this.download(url, Collections.emptyMap());
	}
	
	public String download(String url, Map<String, List<String>> headers) {
		Builder builder = new Builder(url);
		builder.addHeaders(headers);
		Item item = builder.build();
		this.scheduledService.execute(()-> {
			this.client.updateItemOnline(item);
			this.download(item);
		});
		return item.getId();
	}
	
	
	public void download(Item item) {
		RangeUtil range = item.getRangeInfo();
		if (range.isFinish()) {
			Log.log(getClass(), "Download Finish: " + item.getFilename(), item.liteString());
			return;
		}
		
		sessionMonitor.add(range);
		ItemMetaData metaData = null;
		if(range.isStreaming()) {
			metaData = new StreamMetaDataWriter(item);
		}
		else {
			metaData = new ChannelMetaDataWriter(item);
		}
		
//		else if(Integer.MAX_VALUE  > range.getFileLength()) {
//			metaData = new SimpleMappedMetaDataWriter(item);
//		} else {
//			metaData = new LargeMappedMetaDataWriter(item);
//		}

		item.setState(ItemState.INIT_FILE);
		Log.trace(getClass(), item.getFilename(), "Meta Data Writer: " +  metaData.getClass().getSimpleName());
		Log.log(getClass(), "add download item to waiting list", item.toString());
		range.oneCycleDataUpdate();
		wattingList.add(metaData);
		item.setState(ItemState.WAITING);
		dataStore.add(item);
	}
	
	public void download(List<Item> items) {
		items.forEach(this:: download);
	}
	
	
	public void initForDownload(List<Item> items) {
		items.forEach(item -> {
			Item old = dataStore.findByUrlAndSaveDirectory(item.getUrl(), item.getSaveDirectory());
			if (Objects.isNull(old)) {
				client.updateItemOnline(item);
			} else {
				old.getRangeInfo().checkRanges();
				old.addHeaders(item.getHeaders());
				item.copy(old);
			}
			download(item);
		});
	}
	
}
