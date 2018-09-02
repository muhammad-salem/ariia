package org.okaria.okhttp.service;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.log.concurrent.Log;
import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.ItemMetaData;
import org.okaria.mointors.TableItemsMonitor;
import org.okaria.okhttp.client.ChannelClient;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;

public class TableServiceManager extends ServiceManager {

	TableItemsMonitor tableItemsMonitor;
	private Runnable emptyQueueRunnable = ()->{};
	public TableServiceManager(Type type, String proxyHost, int port) {
		super(type, proxyHost, port);
	}

	public TableServiceManager(Proxy proxy) {
		super(proxy);
	}

	public TableServiceManager(CookieJars jar, Proxy proxy) {
		super(jar, proxy);
	}
	
	public TableServiceManager(OkConfig config) {
		super(config);
	}
	public TableServiceManager(Client client) {
		super(client);
	}
	
	protected TableServiceManager() {}
	
	@Override
	protected void initService(Client client) {
		super.initService(client);
		this.tableItemsMonitor = new TableItemsMonitor(sessionMointor);
	}
	
	public static TableServiceManager SegmentServiceManager(Proxy proxy) {
		return SegmentServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static TableServiceManager SegmentServiceManager(OkConfig config) {
		TableServiceManager manager = new TableServiceManager();
		manager.initService(new SegmentClient(config));
		return manager;
	}
	
	public static TableServiceManager ChannelServiceManager(Proxy proxy) {
		return ChannelServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static TableServiceManager ChannelServiceManager(OkConfig config) {
		TableServiceManager manager = new TableServiceManager();
		manager.initService(new ChannelClient(config));
		return manager;
	}
	

//	@Override
//	public void runSystemShutdownHook() {
//		systemFlushDataOnly();
//		saveWattingItemToDisk();
//		//saveDownloadingItemToDisk();
//		printReport();
//	}


	
	@Override
	public void startScheduledService() {
		scheduledService.execute(this::saveWattingItemToDisk);
		
		// for each 2 second
		scheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		scheduledService.scheduleWithFixedDelay(this::systemFlushData, 4, 10, TimeUnit.SECONDS);
		
	}

	@Override
	protected Class<? extends Client> getClientClass() {
		return client.getClass();
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

	@Override
	protected void addDownloadItemEvent(ItemMetaData holder) {
		tableItemsMonitor.add(holder.getRangeMointor());
	}

	@Override
	protected void removeDownloadItemEvent(ItemMetaData holder) {
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
