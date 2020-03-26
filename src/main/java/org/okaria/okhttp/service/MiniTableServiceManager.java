package org.okaria.okhttp.service;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;


import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.ItemMetaData;
import org.okaria.mointors.ItemsMiniTableMonitor;
import org.okaria.okhttp.client.ChannelClient;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;

public class MiniTableServiceManager extends ServiceManager {

	public static MiniTableServiceManager SegmentServiceManager(Proxy proxy) {
		return SegmentServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static MiniTableServiceManager SegmentServiceManager(OkConfig config) {
		return  new MiniTableServiceManager(new SegmentClient(config));
	}
	
	public static MiniTableServiceManager ChannelServiceManager(Proxy proxy) {
		return ChannelServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static MiniTableServiceManager ChannelServiceManager(OkConfig config) {
		return new MiniTableServiceManager(new ChannelClient(config));
	}
	
	ItemsMiniTableMonitor tableItemsMonitor;
	
	private Runnable emptyQueueRunnable = ()->{};
	
	public MiniTableServiceManager(Client client) {
		super(client);
		this.tableItemsMonitor = new ItemsMiniTableMonitor(sessionMointor);
	}
	
	boolean allowDownload = true;
	@Override
	public void startScheduledService() {
		scheduledService.execute(this::saveWattingItemToDisk);

//		scheduledService.scheduleWithFixedDelay(this::checkInternetConnectivity, 0, 1, TimeUnit.SECONDS);
		
		// for each 2 second
		scheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 5, 5, TimeUnit.SECONDS);
	}

	@Override
	public void printReport() {
		System.out.println(tableItemsMonitor.getTableReport());
	}

	@Override
	protected void systemFlushData() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.systemFlush();
		}
	}
	
	
//	@Override
//	public void checkInternetConnectivity() {
//		boolean isDownloading = sessionMointor.isDownloading();
//		
//		if (isDownloading) {
//			allowDownload = true;
//		} else {
//			boolean isOnline = connectivity.isOnline();
//			if (isOnline) {
//				allowDownload = true;
//			} else {
//				allowDownload = false;
//				for (ItemMetaData item : downloadingList) {
//					item.pause();
//					wattingList.add(item);
//				}
//				close();
//			}
//		}
//		Log.info(getClass(), "Network Connectivity", "Allow Download: " + Boolean.toString(allowDownload));
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

	


}
