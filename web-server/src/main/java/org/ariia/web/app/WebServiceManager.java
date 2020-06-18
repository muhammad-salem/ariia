package org.ariia.web.app;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.ItemMetaDataCompleteWrapper;
import org.ariia.items.*;
import org.ariia.logging.Log;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.LiteItem;

public class WebServiceManager extends ServiceManager {

	protected SourceEvent sourceEvent;
	protected EventProvider sessionProvider;
	protected EventProvider itemListProvider;
	protected EventProvider itemProvider;
	
	List<Queue<ItemMetaData>> queues;
	
	public WebServiceManager(Client client, SourceEvent sourceEvent) {
		super(client);
		this.sourceEvent = Objects.requireNonNull(sourceEvent);
		this.sessionProvider = new EventProvider("session-monitor", sourceEvent);
		this.itemListProvider = new EventProvider("item-list", sourceEvent);
		this.itemProvider = new EventProvider("item", sourceEvent);
	}
	
	@Override
	protected void initServiceList(DataStore<Item> dataStore) {
		super.initServiceList(dataStore);

		this.queues = new ArrayList<>(3);
		this.queues.add(waitingList);
		this.queues.add(downloadingList);
		this.queues.add(completingList);
		
	}
	
	@Override
    public void startScheduledService() {
    	dataStore.getAll().forEach(item -> {
			ItemState state = item.getState();
			if(state.isComplete()){
				ItemMetaData metaData = new ItemMetaDataCompleteWrapper(item, properties);
				moveToCompleteList(metaData);
				sessionReport.addRange(item.getRangeInfo());
			} if(state.isDownloading() || state.isPause()){
				moveToPauseList(initItemMetaData(item));
			} else {
				moveToWaitingList(initItemMetaData(item));
			}
		});
    	super.startScheduledService();
		sourceEvent.send("session-start");
    }


	@Override
	public void runSystemShutdownHook() {
		super.runSystemShutdownHook();
		sourceEvent.send("session-shutdown");
	}
	

	@Override
	public void printReport() {
		super.printReport();
		this.sendWebReport();
	}

	private String toJsonItem(ItemMetaData item){
		return Utils.toJson(LiteItem.bind(item));
	}
	private String toJsonItemsList(Stream<ItemMetaData> itemStream){
		return Utils.toJson(
				itemStream
						.map(LiteItem::bind)
						.collect(Collectors.toList())
		);
	}

	private void sendWebReport() {
		try {
			sessionProvider.send(Utils.toJson(sessionReport));
			if (!downloadingList.isEmpty()) {
				itemListProvider.send(toJsonItemsList(downloadingList.stream()));
			}
		} catch (Exception e) {
			Log.error(getClass(), "Send web Report Error", e.getMessage());
		}
	}

	@Override
	protected void waitEvent(ItemMetaData item) {
		itemProvider.send(toJsonItem(item));
	}

	@Override
	protected void pauseEvent(List<ItemMetaData> items) {
		itemListProvider.send(toJsonItemsList(items.stream()));
	}

	@Override
	protected void downloadEvent() { }

	@Override
	protected void completeEvent(List<ItemMetaData> items) {
		itemListProvider.send(toJsonItemsList(items.stream()));
	}
	
	@Override
	public void printAllReport() {
        System.out.println(reportTable.getTableReport());
    }
	
	private Optional<ItemMetaData> searchById(Queue<ItemMetaData> queue, String id) {
		return queue.stream().filter(item -> item.getItem().getId().equals(id)).findAny();
	}
	
	private ItemMetaData find(String id) {
		Optional<ItemMetaData> optional = null;
		ItemMetaData metaData = null;
		for (int i = 0; i < queues.size(); i++) {
			optional = searchById(queues.get(i), id);
			if (optional.isPresent()) {
				metaData = optional.get();
				break;
			}
		}
		return metaData;
	}
	
	private ItemMetaData findAndPause(String id) {
		Optional<ItemMetaData> optional = null;
		ItemMetaData metaData = null;
		for (int i = 0; i < queues.size(); i++) {
			optional = searchById(queues.get(i), id);
			if (optional.isPresent()) {
				metaData = optional.get();
				queues.get(i).remove(metaData);
				metaData.pause();
				metaData.systemFlush();
				break;
			}
		}
		return metaData;
	}
	
	public boolean deleteAndRemoveItem(String id) {
		ItemMetaData metaData = findAndPause(id);
		if (metaData != null) {
			//return false;
//			removeItemEvent(metaData);
			moveToCompleteList(metaData);
			completingList.remove(metaData);
//			sessionReport.removeRange(metaData.getRangeInfo());
			return dataStore.remove(metaData.getItem());
		} else {
			return dataStore.remove(dataStore.findById(id));
		}
		
	}
	
	public boolean pauseItem(String id) {
		ItemMetaData metaData = findAndPause(id);
		return Objects.nonNull(metaData);
	}
	
	public boolean startItem(String id) {
		ItemMetaData metaData = find(id);
		if (Objects.isNull(metaData)) {
			//return false;
			Item item = dataStore.findById(id);
			if(Objects.isNull(item)){
				return false;
			}
			download(item);
			return true;
		}
		if (metaData.isDownloading() || metaData.getRangeInfo().isFinish()) {
			return true;
		} else {
			metaData = findAndPause(id);
			downloadingList.add(metaData);
		}
		return Objects.nonNull(metaData);
	}

	public String download(String url) {
        return this.download(url, Collections.emptyMap());
    }

    public String download(String url, Map<String, List<String>> headers) {
        Builder builder = new Builder(url);
        builder.saveDir(properties.getDefaultSaveDirectory());
        builder.addHeaders(headers);
        Item item = builder.build();
        this.scheduledService.execute(() -> {
            this.client.updateItemOnline(item);
            this.download(item);
        });
        return item.getId();
    }

    public String downloadMetaLink(String[] urls) {
        return this.downloadMetaLink(urls, Collections.emptyMap());
    }

    public String downloadMetaLink(String[] urls, Map<String, List<String>> headers) {
        MetalinkItem metalinkItem = new MetalinkItem();
        metalinkItem.setSaveDirectory(properties.getDefaultSaveDirectory());
        metalinkItem.addHeaders(headers);
        for (String string : urls) {
            metalinkItem.addMirror(string);
        }
        this.scheduledService.execute(() -> {
            this.client.updateItemOnline(metalinkItem);
            this.download(metalinkItem);
        });
        return metalinkItem.getId();
    }
}
