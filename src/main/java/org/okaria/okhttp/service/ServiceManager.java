package org.okaria.okhttp.service;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.manager.ItemStore;
import org.okaria.mointors.SimpleSessionMointor;
import org.okaria.okhttp.client.Client;
import org.okaria.okhttp.writer.ChannelMetaDataWriter;
import org.okaria.okhttp.writer.StreamMetaDataWriter;
import org.okaria.range.RangeUtil;
import org.okaria.setting.Properties;
import org.terminal.console.log.Log;

public abstract class ServiceManager implements Closeable {
	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 10;

	ScheduledExecutorService scheduledService;

	Queue<ItemMetaData> wattingList;
	Queue<ItemMetaData> downloadingList;

	ItemStore itemStore;
	Client client;
	SimpleSessionMointor sessionMointor;
	NetworkConnectivity connectivity;
	
	public ServiceManager(Client client) {
		this.client = client;
		this.itemStore = ItemStore.CreateAndInitStore();
		this.wattingList 	= new LinkedList<>();
		this.downloadingList = new LinkedList<>();
		this.sessionMointor = new SimpleSessionMointor(); 
		this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		this.connectivity = new NetworkConnectivity(client);
	}
	

	@Override
	public void close() {
		client.getExecutorService().shutdownNow();
		scheduledService.shutdownNow();
	}
		
	public boolean isNetworkFailer() {
		if (sessionMointor.isDownloading()) {
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
//		} else {
//			metaData = new LargeMappedMetaDataWriter(item);
//		}
		
		range.oneCycleDataUpdate();
		wattingList.add(metaData);
	}
	
	protected void checkdownloadList() {
		if (isNetworkFailer()) {
			for (ItemMetaData item : downloadingList) {
				item.pause();
				downloadingList.remove(item);
				removeItemEvent(item);
				wattingList.add(item);
			}
			Log.info(getClass(), "Check Network Connection",
					"Network Connectivity Statues: NETWORK DISCONNECTED");
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
				metaData.startDownloadQueue(client, sessionMointor);
				
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
				getFinishDownloadQueueEvent().run();
			}
		}
		
	}

	public    abstract void printReport();
	protected abstract void systemFlushData();
	protected abstract void saveWattingItemToDisk();
	protected abstract void saveDownloadingItemToDisk();
	protected abstract void addItemEvent(ItemMetaData holder);
	protected abstract void removeItemEvent(ItemMetaData holder);
	
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
