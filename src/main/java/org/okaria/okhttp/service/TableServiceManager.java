package org.okaria.okhttp.service;

import java.net.Proxy;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.ItemMetaData;
import org.okaria.mointors.TableItemsMonitor;
import org.okaria.okhttp.client.ChannelClient;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;
import org.terminal.console.log.Log;

public class TableServiceManager extends ServiceManager {

	public static TableServiceManager SegmentServiceManager(Proxy proxy) {
		return SegmentServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static TableServiceManager SegmentServiceManager(OkConfig config) {
		return new TableServiceManager(new SegmentClient(config));
	}
	
	public static TableServiceManager ChannelServiceManager(Proxy proxy) {
		return ChannelServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static TableServiceManager ChannelServiceManager(OkConfig config) {
		return new TableServiceManager(new ChannelClient(config));
	}
	
	
	TableItemsMonitor tableItemsMonitor;
//	NetworkConnectivity connectivity;
	
	private Runnable emptyQueueRunnable = ()->{};

	public TableServiceManager(Client client) {
		super(client);
		this.tableItemsMonitor = new TableItemsMonitor(sessionMointor);
//		this.connectivity = new NetworkConnectivity(client);
	}
	


	
	ScheduledFuture<?> downloadServices;

	@Override
	public void startScheduledService() {
		scheduledService.execute(this::saveWattingItemToDisk);
//		scheduledService.scheduleWithFixedDelay(this::checkInternetConnectivity, 3, 1, TimeUnit.MINUTES);
		
		// for each 2 second
		downloadServices = scheduledService
				.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void printReport() {
		Future<?> future = scheduledService.submit(()->{
			System.out.println(tableItemsMonitor.getTableReport());
			});
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			future.cancel(true);
		} catch (InterruptedException e) {
			Log.info(getClass(), "print report problem", e.getMessage());
		}
	}

	@Override
	protected void systemFlushData() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.systemFlush();
		}
		//saveDownloadingItemToDisk();
	}
	
//	@Override
//	public void checkInternetConnectivity() {
//		
//		if (sessionMointor.isDownloading()) {
//			boolean isOnline = connectivity.isOnline();
//			if (isOnline) {
//				if (Objects.isNull(downloadServices)) {
//					Log.info(getClass() , "start schedule download Services");
//					downloadServices = scheduledService
//							.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
//				}
//			} else {
//				for (ItemMetaData item : downloadingList) {
//					item.pause();
//					wattingList.add(item);
//				}
//				downloadServices.cancel(true);
//				downloadServices = null;
//			}
//			Log.info(getClass(), "network connectivity", 
//					"isOnline: ".concat( Boolean.toString(isOnline))
//					+  "\nisDownloading(): ".concat(Boolean.toString(sessionMointor.isDownloading())));
//		}
//	}


	@Override
	protected void addItemEvent(ItemMetaData holder) {
		tableItemsMonitor.add(holder.getRangeMointor());
	}

	@Override
	protected void removeItemEvent(ItemMetaData holder) {
		tableItemsMonitor.remove(holder.getRangeMointor());
	}

	@Override
	public void saveDownloadingItemToDisk() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.saveItem2CacheFile();
		}
	}
	@Override
	public void saveWattingItemToDisk() {
		for (ItemMetaData placeHolder : wattingList) {
			placeHolder.saveItem2CacheFile();
		}
	}

	public void setFinishDownloadQueueEvent(Runnable emptyQueueRunnable) {
		this.emptyQueueRunnable = emptyQueueRunnable;
	}
	
	@Override
	public Runnable getFinishDownloadQueueEvent() {
		return emptyQueueRunnable;
	}

//	@Override
//	public void warrpItem(Item item) {
//		sessionMointor.add(item.getRangeInfo());
//		wattingList.add(new LargeMappedMetaDataWriter(item));
//	}


}
