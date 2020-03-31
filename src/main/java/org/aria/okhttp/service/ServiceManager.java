package org.aria.okhttp.service;

import java.io.Closeable;
import java.net.Proxy;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.aria.core.CookieJars;
import org.aria.core.OkConfig;
import org.aria.logging.Log;
import org.aria.manager.Item;
import org.aria.manager.ItemMetaData;
import org.aria.manager.ItemStore;
import org.aria.monitors.MiniTableMonitor;
import org.aria.monitors.SessionMonitor;
import org.aria.monitors.SimpleSessionMonitor;
import org.aria.monitors.TableMonitor;
import org.aria.network.ConnectivityCheck;
import org.aria.network.UrlConnectivity;
import org.aria.okhttp.client.ChannelClient;
import org.aria.okhttp.client.Client;
import org.aria.okhttp.client.SegmentClient;
import org.aria.okhttp.writer.ChannelMetaDataWriter;
import org.aria.okhttp.writer.StreamMetaDataWriter;
import org.aria.range.RangeUtil;
import org.aria.setting.Properties;

public class ServiceManager implements Closeable {
	
	public static ServiceManager SegmentServiceManager(Proxy proxy) {
		return SegmentServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static ServiceManager SegmentServiceManager(OkConfig config) {
		return  new ServiceManager(new SegmentClient(config));
	}
	
	public static ServiceManager ChannelServiceManager(Proxy proxy) {
		return ChannelServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static ServiceManager ChannelServiceManager(OkConfig config) {
		return new ServiceManager(new ChannelClient(config));
	}
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	ScheduledExecutorService scheduledService;

	Queue<ItemMetaData> wattingList;
	Queue<ItemMetaData> downloadingList;

	ItemStore itemStore;
	Client client;
	SessionMonitor sessionMonitor;
	TableMonitor reportTable;
	ConnectivityCheck connectivity;
	

	private Runnable finishAction = ()->{};
	
	public ServiceManager(Client client) {
		this.client = client;
		this.sessionMonitor = new SimpleSessionMonitor(); 
		this.reportTable = new MiniTableMonitor(sessionMonitor);
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new UrlConnectivity(client.getHttpClient().proxy());

		this.itemStore = ItemStore.CreateAndInitStore();
		this.wattingList 	= new LinkedList<>();
		this.downloadingList = new LinkedList<>();
	}
	
	public ServiceManager(Client client, TableMonitor reportTable) {
		this.client = client;
		this.reportTable = reportTable;
		this.sessionMonitor = reportTable.getSessionMonitor(); 
		this.connectivity = new UrlConnectivity(client.getHttpClient().proxy());

		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.itemStore = ItemStore.CreateAndInitStore();
		this.wattingList 	= new LinkedList<>();
		this.downloadingList = new LinkedList<>();
	}
	
	public ServiceManager(Client client, SimpleSessionMonitor monitor,
			ConnectivityCheck connectivity, TableMonitor reportTable) {
		this.client = client;
		this.sessionMonitor = monitor; 
		this.reportTable = reportTable;
		this.connectivity = connectivity;

		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.itemStore = ItemStore.CreateAndInitStore();
		this.wattingList 	= new LinkedList<>();
		this.downloadingList = new LinkedList<>();
	}
	
	
//	@Override
	public void startScheduledService() {
		scheduledService.execute(this::saveWattingItemToDisk);

//		scheduledService.scheduleWithFixedDelay(this::checkInternetConnectivity, 0, 1, TimeUnit.SECONDS);
		
		// for each 2 second
		scheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 1, 5, TimeUnit.SECONDS);
	}

	@Override
	public void close() {
		client.getExecutorService().shutdownNow();
		scheduledService.shutdownNow();
	}
		
	public boolean isNetworkFailer() {
		if (sessionMonitor.isDownloading()) {
			return false;
		} else {
			if (connectivity.isOnline()) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	protected void checkdownloadList() {
		if (isNetworkFailer()) {
			Log.info(getClass(), "Check Network Connection",
					"Network Connectivity Statues: NETWORK DISCONNECTED");
			for (ItemMetaData item : downloadingList) {
				item.pause();
				downloadingList.remove(item);
//				removeItemEvent(item);
				wattingList.add(item);
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
					Log.info(getClass(), "items added to download list", builder.toString());
				}
			}
			
			for (ItemMetaData metaData : downloadingList) {
				Item item = metaData.getItem();
				RangeUtil info = item.getRangeInfo();
				if (info.isFinish()) {
					removeItemEvent(metaData);
					Log.info(getClass(), "Download Finish: " + metaData.getItem().getFilename(),
							metaData.getItem().liteString());
					continue;
				} else if(! metaData.isDownloading()) {
					metaData.startDownloadQueue(client, sessionMonitor);
				}
				metaData.checkCompleted();
				
			}
			
			if (downloadingList.isEmpty() & wattingList.isEmpty()) {
				finishAction.run();
			}
		}
		
	}
	
	
	protected void addItemEvent(ItemMetaData metaData) {
		metaData.initWaitQueue();
		metaData.checkCompleted();
		downloadingList.add(metaData);
		metaData.startDownloadQueue(client, sessionMonitor);
		reportTable.add(metaData.getRangeMointor());
		metaData.getItem().getRangeInfo().oneCycleDataUpdate();
	}

	protected void removeItemEvent(ItemMetaData metaData) {
		metaData.systemFlush();
		metaData.saveItem2CacheFile();
		metaData.close();
		downloadingList.remove(metaData);
		reportTable.remove(metaData.getRangeMointor());
	}
	
	public void runSystemShutdownHook() {
		for (ItemMetaData metaData : downloadingList) {
			metaData.systemFlush();
			metaData.close();
		}
		
		for (ItemMetaData metaData : wattingList) {
			metaData.saveItem2CacheFile();
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

	public ItemStore getItemStore() {
		return itemStore;
	}

	public void setItemStore(ItemStore itemStore) {
		this.itemStore = itemStore;
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

	protected void systemFlushData() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.systemFlush();
		}
	}
	



	protected void saveDownloadingItemToDisk() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.saveItem2CacheFile();
		}
	}

	protected void saveWattingItemToDisk() {
		for (ItemMetaData placeHolder : wattingList) {
			placeHolder.saveItem2CacheFile();
		}
	}
	
	
	public void download(Item item) {
		RangeUtil range = item.getRangeInfo();
		if (range.isFinish()) {
			Log.info(getClass(), "Download Finish: " + item.getFilename(), item.liteString());
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
		
		Log.trace(getClass(), item.getFilename(), "Meta Data Writer: " +  metaData.getClass().getSimpleName());
		Log.info(getClass(), "add download item to waiting list", item.toString());
		range.oneCycleDataUpdate();
		wattingList.add(metaData);
	}
	
	public void download(Set<Item> items) {
		items.forEach(this:: download);
	}
	
	
	public void initForDownload(Set<Item> items) {
		items.forEach(item -> {
			Item old = itemStore.searchByUrl(item.getUrl());
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
