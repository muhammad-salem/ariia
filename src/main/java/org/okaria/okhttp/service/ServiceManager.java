package org.okaria.okhttp.service;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.terminal.console.log.Log;
import org.okaria.core.CookieJars;
import org.okaria.core.OkConfig;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.manager.ItemStore;
import org.okaria.mointors.SimpleSessionMointor;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.client.SegmentClient;
import org.okaria.okhttp.writer.ChannelMetaDataWriter;
import org.okaria.okhttp.writer.StreamMetaDataWriter;
import org.okaria.range.RangeUtil;
import org.okaria.setting.Properties;

public abstract class ServiceManager implements Closeable {
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	ScheduledExecutorService scheduledService;

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
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
	}
	
	public boolean checkInternetConnectivity() {
		try {
			Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
			while ( eni.hasMoreElements()) {
				NetworkInterface ni = eni.nextElement();
				if (ni.isUp() && ni.getParent() == null) {
					return true;
				}
			}
		} catch (SocketException e) {
			return false;
		}
//		return false;
		return sessionMointor.speedOfTCPReceive() > 0l;
	}
	

	@Override
	public void close() {
		client.getExecutorService().shutdown();
		scheduledService.shutdown();
	}
	
	
	public void warrpItem(Item item) {
		RangeUtil range = item.getRangeInfo();
		sessionMointor.add(range);
		ItemMetaData metaData = null;
		if(range.isStreaming()) {
			metaData = new StreamMetaDataWriter(item);
		}
		else {
			metaData = new ChannelMetaDataWriter(item);
		}
		
//		else if(Integer.MAX_VALUE  > range.getFileLength()) {
//			metaData = new SimpleMappedMetaDataWriter(item);
//		}else {
//			metaData = new LargeMappedMetaDataWriter(item);
//		}
		
		range.oneCycleDataUpdate();
		wattingList.add(metaData);
	}
	
	protected void checkdownloadList() {
		if(downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL) {
			StringBuilder builder = new StringBuilder();
			while (downloadingList.size() < Properties.MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
				ItemMetaData metaData = wattingList.poll();
				downloadingList.add(metaData);
				addDownloadItemEvent(metaData);
				metaData.getItem().getRangeInfo().oneCycleDataUpdate();
				builder.append(metaData.getItem().getFilename());
				builder.append('\t');
				builder.append( metaData.getItem().getRangeInfo().getRemainingLengthMB());
				builder.append(' ');
				builder.append('\n');
			}
			if(builder.length() != 0) {
				builder.delete(builder.length()-2, builder.length());
				Log.info(getClass(), "items added to download list", builder.toString());
			}
		}
		
		
		List<ItemMetaData> removeList = new ArrayList<>();
		for (ItemMetaData metaData : downloadingList) {
			Item item = metaData.getItem();
			RangeUtil info = item.getRangeInfo();
			if (info.isFinish()) {
				Log.info(getClass(), "Remove Item from download list", item.getFilename());
				removeList.add(metaData);
				continue;
			} else if(! metaData.isDownloading()) {
//				metaData.download(client, sessionMointor);
				metaData.downloadThreads(client, sessionMointor);
			}
//			metaData.recycelCompletedFromMax(client, sessionMointor);
			metaData.checkCompleted(client, sessionMointor);
			
		}
		
		// remove 
		removeList.forEach((metaData)->{
			metaData.getItem().getRangeInfo().oneCycleDataUpdate();
			metaData.systemFlush();
			Log.info(getClass(), "Download Complete", metaData.getItem().liteString());
			downloadingList.remove(metaData);
			removeDownloadItemEvent(metaData);
			metaData.saveItem2CacheFile();
			metaData.close();
		});
		
		if (downloadingList.isEmpty() & wattingList.isEmpty()) {
			getFinishDownloadQueueEvent().run();
		}
		
//		if (! checkInternetConnectivity()) {
//			close();
//		}
		
	}

	protected abstract Class<? extends Client> getClientClass();
	public    abstract void printReport();
	protected abstract void systemFlushData();
	protected abstract void saveWattingItemToDisk();
	protected abstract void saveDownloadingItemToDisk();
	protected abstract void addDownloadItemEvent(ItemMetaData holder);
	protected abstract void removeDownloadItemEvent(ItemMetaData holder);
	
	public abstract Runnable getFinishDownloadQueueEvent();
	
	public abstract void startScheduledService();
	
	public void runSystemShutdownHook() {
		for (ItemMetaData metaData : downloadingList) {
			metaData.systemFlush();
			metaData.close();
		}
		for (ItemMetaData metaData : wattingList) {
			metaData.saveItem2CacheFile();
			metaData.close();
		}
//		printReport();
	}
	
	
	
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

	public SimpleSessionMointor getSessionMointor() {
		return sessionMointor;
	}

	public void setSessionMointor(SimpleSessionMointor monitor) {
		this.sessionMointor = monitor;
	}

	
}
