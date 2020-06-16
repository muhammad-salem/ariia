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

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.writer.ChannelMetaDataWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.StreamMetaDataWriter;
import org.ariia.items.Builder;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.items.ItemStore;
import org.ariia.items.MetalinkItem;
import org.ariia.logging.Log;
import org.ariia.monitors.MiniSpeedTableReport;
import org.ariia.monitors.SessionReport;
import org.ariia.monitors.SimpleSessionReport;
import org.ariia.monitors.SpeedTableReport;
import org.ariia.network.ConnectivityCheck;
import org.ariia.network.NetworkReport;
import org.ariia.network.UrlConnectivity;
import org.ariia.range.RangeUtil;

public class ServiceManager implements Closeable {
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	protected ScheduledExecutorService scheduledService;

	protected Queue<ItemMetaData> wattingList;
	protected Queue<ItemMetaData> downloadingList;
	protected Queue<ItemMetaData> completeingList;

	protected DataStore<Item> dataStore;
	protected Client client;
	protected SessionReport sessionReport;
	protected SpeedTableReport reportTable;
	protected ConnectivityCheck connectivity;
	
	protected Properties properties;

	private Runnable finishAction = ()->{};
	
	public ServiceManager(Client client) {
		this.client = client;
		this.sessionReport = new SimpleSessionReport(); 
		this.reportTable = new MiniSpeedTableReport(sessionReport);
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new UrlConnectivity(client.getProxy());
		this.initServiceList(new ItemStore());
		
	}
	
	public ServiceManager(Client client, DataStore<Item> dataStore) {
		this.client = client;
		this.sessionReport = new SimpleSessionReport(); 
		this.reportTable = new MiniSpeedTableReport(sessionReport);
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new UrlConnectivity(client.getProxy());
		this.initServiceList(dataStore);
	}
	
	public ServiceManager(Client client, SpeedTableReport reportTable) {
		this.client = client;
		this.reportTable = reportTable;
		this.sessionReport = reportTable.getSessionMonitor(); 
		this.connectivity = new UrlConnectivity(client.getProxy());

		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.initServiceList(new ItemStore());
	}
	
	public ServiceManager(Client client, SessionReport monitor, SpeedTableReport reportTable) {
		this(client, monitor, new UrlConnectivity(client.getProxy()), reportTable);
	}
	
	public ServiceManager(Client client, SessionReport monitor,
			ConnectivityCheck connectivity, SpeedTableReport reportTable) {
		this.client = client;
		this.sessionReport = monitor; 
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
		this.properties = client.getProperties();
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
		if (sessionReport.isDownloading()) {
			return false;
		} else if (downloadingList.isEmpty() & wattingList.isEmpty()) {
			return false;
		} else {
			NetworkReport report = connectivity.networkReport();
			Log.trace(connectivity.getClass(), report.getTitle(), report.getMessage());
			//if (NetworkStatus.Connected.equals(report.getNetworkStatus())) {
			if(report.isConnected()) {
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
			if(downloadingList.size() < properties.getMaxActiveDownloadPool()) {
				StringBuilder builder = new StringBuilder();
				while (downloadingList.size() < properties.getMaxActiveDownloadPool() && !wattingList.isEmpty()) {
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
				metaData.startAndCheckDownloadQueue(client, sessionReport.getMointor());
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
		metaData.startAndCheckDownloadQueue(client, sessionReport.getMointor());
		reportTable.add(metaData.getRangeReport());
		metaData.getItem().getRangeInfo().oneCycleDataUpdate();
	}

	protected void removeItemEvent(ItemMetaData metaData) {
		metaData.systemFlush();
		metaData.getItem().setState(ItemState.COMPLETE);
		dataStore.save(metaData.getItem());
		metaData.close();
		downloadingList.remove(metaData);
		reportTable.remove(metaData.getRangeReport());
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

	public SessionReport getSessionMointor() {
		return sessionReport;
	}

	public void setSessionMointor(SimpleSessionReport monitor) {
		this.sessionReport = monitor;
	}
	
	
	public void printReport() {
		System.out.println(reportTable.getTableReport());
	}
	
	public void printAllReport() {
		for (ItemMetaData itemMetaData : completeingList) {
			reportTable.add(itemMetaData.getRangeReport());
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
		builder.saveDir(properties.getDefaultSaveDirectory());
		builder.addHeaders(headers);
		Item item = builder.build();
		this.scheduledService.execute(()-> {
			this.client.updateItemOnline(item);
			this.download(item);
		});
		return item.getId();
	}
	
	public String downloadMetalink(String[] urls) {
		return this.downloadMetalink(urls, Collections.emptyMap());
	}
	public String downloadMetalink(String[] urls, Map<String, List<String>> headers) {
		MetalinkItem metalinkItem = new MetalinkItem();
		metalinkItem.setSaveDirectory(properties.getDefaultSaveDirectory());
		metalinkItem.addHeaders(headers);
		for (String string : urls) {
			metalinkItem.addMirror(string);
		}
		this.scheduledService.execute(()-> {
			this.client.updateItemOnline(metalinkItem);
			this.download(metalinkItem);
		});
		return metalinkItem.getId();
	}
	
	
	public void download(Item item) {
		RangeUtil range = item.getRangeInfo();
		if (range.isFinish()) {
			Log.log(getClass(), "Download Finish: " + item.getFilename(), item.liteString());
			return;
		}
		
		sessionReport.addRange(range);
		ItemMetaData metaData = null;
		if(range.isStreaming()) {
			metaData = new StreamMetaDataWriter(item, properties);
		}
		else {
			metaData = new ChannelMetaDataWriter(item, properties);
		}
		
//		else if(Integer.MAX_VALUE  > range.getFileLength()) {
//			metaData = new SimpleMappedMetaDataWriter(item, properties);
//		} else {
//			metaData = new LargeMappedMetaDataWriter(item, properties);
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
