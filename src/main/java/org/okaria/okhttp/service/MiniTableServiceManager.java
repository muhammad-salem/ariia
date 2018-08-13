package org.okaria.okhttp.service;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;

import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.mointors.ItemsMiniTableMonitor;
import org.okaria.okhttp.client.ChannelClient;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;
import org.okaria.okhttp.writer.LargeMappedMetaDataWriter;

public class MiniTableServiceManager extends ServiceManager {

	ItemsMiniTableMonitor tableItemsMonitor;
	private Runnable emptyQueueRunnable = ()->{};
	public MiniTableServiceManager(Type type, String proxyHost, int port) {
		super(type, proxyHost, port);
	}

	public MiniTableServiceManager(Proxy proxy) {
		super(proxy);
	}

	public MiniTableServiceManager(CookieJars jar, Proxy proxy) {
		super(jar, proxy);
	}
	
	public MiniTableServiceManager(OkConfig config) {
		super(config);
	}
	public MiniTableServiceManager(Client client) {
		super(client);
	}
	
	protected MiniTableServiceManager() {}
	
	@Override
	protected void initService(Client client) {
		super.initService(client);
		this.tableItemsMonitor = new ItemsMiniTableMonitor(sessionMointor);
	}
	

	public static MiniTableServiceManager SegmentServiceManager(Proxy proxy) {
		return SegmentServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static MiniTableServiceManager SegmentServiceManager(OkConfig config) {
		MiniTableServiceManager manager = new MiniTableServiceManager();
		manager.initService(new SegmentClient(config));
		return manager;
	}
	
	public static MiniTableServiceManager ChannelServiceManager(Proxy proxy) {
		return ChannelServiceManager(new OkConfig(CookieJars.CookieJarMap, proxy));
	}
	public static MiniTableServiceManager ChannelServiceManager(OkConfig config) {
		MiniTableServiceManager manager = new MiniTableServiceManager();
		manager.initService(new ChannelClient(config));
		return manager;
	}
	

	@Override
	public void getSystemShutdownHook() {
		systemFlushDataOnly();
		saveWattingItemToDisk();
		//saveDownloadingItemToDisk();
		printReport();
	}


	
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
	protected void printReport() {
		System.out.println(tableItemsMonitor.getTableReport());
	}

	@Override
	protected void systemFlushData() {
		for (ItemMetaData placeHolder : downloadingList) {
			placeHolder.systemFlush();
			placeHolder.saveItem2CacheFile();
		}
		//saveDownloadingItemToDisk();
	}
	protected void systemFlushDataOnly() {
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

	public void setEmptyQueueRunnable(Runnable emptyQueueRunnable) {
		this.emptyQueueRunnable = emptyQueueRunnable;
	}
	
	@Override
	public Runnable getEmptyQueueEvent() {
		return emptyQueueRunnable;
	}

	@Override
	public void warrpItem(Item item) {
		sessionMointor.add(item.getRangeInfo());
		wattingList.add(new LargeMappedMetaDataWriter(item));
	}


}
