package org.okaria.okhttp.service;

import java.io.Closeable;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.manager.ItemStore;
import org.okaria.monitors.MiniTableMonitor;
import org.okaria.monitors.SessionMonitor;
import org.okaria.monitors.SimpleSessionMonitor;
import org.okaria.monitors.TableMonitor;
import org.okaria.network.ConnectivityCheck;
import org.okaria.network.UrlConnectivity;
import org.okaria.okhttp.client.ChannelClient;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;
import org.okaria.okhttp.writer.ChannelMetaDataWriter;
import org.okaria.okhttp.writer.StreamMetaDataWriter;
import org.okaria.range.RangeUtil;
import org.okaria.setting.Properties;
import org.terminal.console.log.Log;

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
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 5, 5, TimeUnit.SECONDS);
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
	
	
	public void addItemToWattingList(Item item) {
		RangeUtil range = item.getRangeInfo();
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
		
		range.oneCycleDataUpdate();
		wattingList.add(metaData);
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
				while (downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
					ItemMetaData metaData = wattingList.poll();
					downloadingList.add(metaData);
					addItemEvent(metaData);
					metaData.getItem().getRangeInfo().oneCycleDataUpdate();

					StringBuilder builder = new StringBuilder();
					builder.append(metaData.getItem().getFilename());
					builder.append("\tRemaining: ");
					builder.append( metaData.getItem().getRangeInfo().getRemainingLengthMB());
					Log.info(getClass(), "add item to download list", builder.toString());
				}
			}
			
			List<ItemMetaData> removeList = new ArrayList<>();
			
			for (ItemMetaData metaData : downloadingList) {
				Item item = metaData.getItem();
				RangeUtil info = item.getRangeInfo();
				if (info.isFinish()) {
					Log.info(getClass(), "finish download URL", item.getFilename());
					removeList.add(metaData);
					continue;
				}
				metaData.initWaitQueue();
				metaData.checkCompleted();
				metaData.startDownloadQueue(client, sessionMonitor);
				
			}
			
			// remove 
			removeList.forEach((metaData)->{
				metaData.getItem().getRangeInfo().oneCycleDataUpdate();
				metaData.systemFlush();
				Log.info(getClass(), "Download Complete", metaData.getItem().liteString());
				downloadingList.remove(metaData);
				removeItemEvent(metaData);
				metaData.saveItem2CacheFile();
				metaData.close();
			});
			
			if (downloadingList.isEmpty() & wattingList.isEmpty()) {
				close();
				finishAction.run();
			}
		}
		
	}

//	public    abstract void printReport();
//	protected abstract void systemFlushData();
//	protected abstract void saveWattingItemToDisk();
//	protected abstract void saveDownloadingItemToDisk();
//	protected abstract void addItemEvent(ItemMetaData holder);
//	protected abstract void removeItemEvent(ItemMetaData holder);
//	public	  abstract void startScheduledService();
	
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
	
	
	public void setFinishDownloadAction(Runnable runnable) {
		this.finishAction = runnable;
	}
	

//	public Runnable getFinishDownloadQueueEvent() {
//		return emptyQueueRunnable;
//	}
	
	
	public ScheduledExecutorService getCheduledService() {
		return scheduledService;
	}

	public void setCheduledService(ScheduledExecutorService cheduledService) {
		this.scheduledService = cheduledService;
	}

	public Queue<ItemMetaData> getWattingList() {
		return wattingList;
	}

	public void setWattingList(Queue<ItemMetaData> wattingList) {
		this.wattingList = wattingList;
	}

	public Queue<ItemMetaData> getDownloadingList() {
		return downloadingList;
	}

	public void setDownloadingList(Queue<ItemMetaData> downloadingList) {
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
	
	
//	@Override
	public void printReport() {
		System.out.println(reportTable.getTableReport());
	}

//	@Override
	protected void systemFlushData() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.systemFlush();
		}
	}
	

//	@Override
	protected void addItemEvent(ItemMetaData holder) {
		reportTable.add(holder.getRangeMointor());
	}

//	@Override
	protected void removeItemEvent(ItemMetaData holder) {
		reportTable.remove(holder.getRangeMointor());
	}

//	@Override
	public void saveDownloadingItemToDisk() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.saveItem2CacheFile();
		}
	}
//	@Override
	public void saveWattingItemToDisk() {
		for (ItemMetaData placeHolder : wattingList) {
			placeHolder.saveItem2CacheFile();
		}
	}

	
}
