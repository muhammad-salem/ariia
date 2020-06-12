package org.ariia.web.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.ItemMetaDataCompleteWarpper;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.LiteItem;

public class WebServiceManager extends ServiceManager {

	protected EventProvider sessionProvider;

	protected EventProvider wattingItemProvider;
	protected EventProvider downloadingItemProvider;
	protected EventProvider completeingItemProvider;
	
	List<Queue<ItemMetaData>> queues;
	
	protected int trackWating = 0;
	protected int trackComplete = 0;
	
	public WebServiceManager(Client client, SourceEvent sourceEvent) {
		super(client);
		sourceEvent = Objects.requireNonNull(sourceEvent);
		this.sessionProvider = new EventProvider("event-session", sourceEvent);
		this.wattingItemProvider = new EventProvider("event-item-watting", sourceEvent);
		this.downloadingItemProvider = new EventProvider("event-item-download", sourceEvent);
		this.completeingItemProvider = new EventProvider("event-item-complete", sourceEvent);
		
		this.queues = new ArrayList<>(3);
		this.queues.add(wattingList);
		this.queues.add(downloadingList);
		this.queues.add(completeingList);
	}
	
	@Override
	protected void initServiceList(DataStore<Item> dataStore) {
		super.initServiceList(dataStore);
		dataStore.getAll().forEach(item -> {
			if(item.getState().isComplete()){
				completeingList.add(new ItemMetaDataCompleteWarpper(item, properties));
			} else {
				download(item);
			}
		});
	}
	
	@Override
	public void printReport() {
		super.printReport();
		this.sendwebReport();
	}
	
	private void sendwebReport() {
		sessionProvider.send(Utils.toJson(sessionMonitor));
		if (wattingList.size() != trackWating) {
			wattingItemProvider.send(Utils.toJson(wattingList.stream().map(LiteItem::bind).collect(Collectors.toList())));
			trackWating = wattingList.size();
		}
		if (!downloadingList.isEmpty()) {
			downloadingItemProvider.send(Utils.toJson(downloadingList.stream().map(LiteItem::bind).collect(Collectors.toList())));
		}
		if (completeingList.size() != trackComplete) {
			completeingItemProvider.send(Utils.toJson(completeingList.stream().map(LiteItem::bind).collect(Collectors.toList())));			
			trackComplete = completeingList.size();
		}
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
			removeItemEvent(metaData);
			completeingList.remove(metaData);
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
	
}
