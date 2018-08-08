package org.okaria.okhttp.service;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.log.concurrent.Log;
import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.manager.ItemStore;
import org.okaria.mointors.SimpleSessionMointor;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;
import org.okaria.range.RangeInfo;
import org.okaria.setting.Properties;

public abstract class ServiceManager implements Closeable {
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	ScheduledExecutorService cheduledService;

	Queue<ItemMetaData> wattingList;
	Queue<ItemMetaData> downloadingList;

	ItemStore itemStore;
	Client client;
	SimpleSessionMointor sessionMointor;
	
	
	public ServiceManager(Proxy.Type type, String proxyHost, int port) {
		this(new Proxy(type, new InetSocketAddress(proxyHost, port)));
	}

	public ServiceManager(Proxy proxy) {
		this(CookieJars.CookieJarMap, proxy);
	}
	public ServiceManager(CookieJars jar,Proxy proxy) {
		this(new OkConfig(jar, proxy));
	}
	
	public ServiceManager(OkConfig config) {
		Class< ? extends Client> clientClass = getClientClass();
		Client client = null;
		try {
			client = clientClass.getConstructor(OkConfig.class).newInstance(config);
		} catch (Exception e) {
			client = new SegmentClient(config);
		}
		initService(client);
	}
	
	protected ServiceManager() {}
	

	public ServiceManager(Client client) {
		initService(client);
	}


	/**
	 * 
	 */
	protected void initService(Client client) {
		this.client = client;
		this.itemStore = ItemStore.CreateAndInitStore();
		this.wattingList 	= new LinkedList<>();
		this.downloadingList = new LinkedList<>();
		this.sessionMointor = new SimpleSessionMointor(); 
		this.cheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
	}
	

	@Override
	public void close() {
		cheduledService.shutdown();
	}
	
//	public void warrpItem(Item item) {
//		sessionMointor.add(item.getRangeInfo());
//		wattingList.add(new ItemMetaData(item));
//	}
	
	protected void checkdownloadList() {
		if(downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL) {
			StringBuilder builder = new StringBuilder();
			while (downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
				ItemMetaData placeHolder = wattingList.poll();
				downloadingList.add(placeHolder);
				addDownloadItemEvent(placeHolder);
				placeHolder.getItem().getRangeInfo().oneCycleDataUpdate();
				builder.append(placeHolder.getItem().getFilename());
				builder.append('\t');
				builder.append( placeHolder.getItem().getRangeInfo().getRemainingLengthMB());
				builder.append(' ');
				builder.append('\n');
			}
			if(builder.length() != 0) {
				builder.delete(builder.length()-2, builder.length());
				Log.info(getClass(), "items added to download list", builder.toString());
			}
		}
		
		
		List<ItemMetaData> removeList = new ArrayList<>();
		for (ItemMetaData placeHolder : downloadingList) {
			Item item = placeHolder.getItem();
			RangeInfo info = item.getRangeInfo();
			if (info.isFinish()) {
				Log.info(getClass(), "Remove Item from download list", item.getFilename());
				removeList.add(placeHolder);
				continue;
			}else if(! placeHolder.isDownloading()) {
				placeHolder.download(client, sessionMointor/*, indictor.getItemMointor()*/);
			}
			placeHolder.checkDoneFuturesFromMax(client, sessionMointor/*, indictor.getItemMointor()*/);
			
		}
		
		// remove 
		removeList.forEach((placeHolder)->{
			placeHolder.getItem().getRangeInfo().oneCycleDataUpdate();
			placeHolder.systemFlush();
			Log.info(getClass(), "Download Complete", placeHolder.getItem().liteString());
			downloadingList.remove(placeHolder);
			removeDownloadItemEvent(placeHolder);
			placeHolder.saveItem2CacheFile();
			placeHolder.close();
		});
		
		if (downloadingList.isEmpty() & wattingList.isEmpty()) {
			getEmptyQueueEvent().run();
		}
	}

	protected abstract Class<? extends Client> getClientClass();
	protected abstract void printReport();
	protected abstract void systemFlushData();
	protected abstract void saveWattingItemToDisk();
	protected abstract void saveDownloadingItemToDisk();
	protected abstract void addDownloadItemEvent(ItemMetaData holder);
	protected abstract void removeDownloadItemEvent(ItemMetaData holder);
	
	public abstract Runnable getEmptyQueueEvent();
	public abstract void getSystemShutdownHook();
	public abstract void startScheduledService();
	
	public abstract void warrpItem(Item item);
	
	public ScheduledExecutorService getCheduledService() {
		return cheduledService;
	}

	public void setCheduledService(ScheduledExecutorService cheduledService) {
		this.cheduledService = cheduledService;
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

	public SimpleSessionMointor getSessionMointor() {
		return sessionMointor;
	}

	public void setSessionMointor(SimpleSessionMointor monitor) {
		this.sessionMointor = monitor;
	}

	
}
