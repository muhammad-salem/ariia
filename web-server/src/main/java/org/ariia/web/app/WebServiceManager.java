package org.ariia.web.app;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.*;
import org.ariia.logging.Log;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.WebItem;

public class WebServiceManager extends ServiceManager {

	protected SourceEvent sourceEvent;
	protected EventProvider sessionProvider;
	protected EventProvider itemListProvider;
	protected EventProvider itemProvider;

	List<Queue<ItemMetaData>> queues;

	protected boolean listPaused = true;

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

	public boolean isListPaused() {
		return listPaused;
	}

	public boolean setListPaused(boolean listPaused) {
		return this.listPaused = listPaused;
	}


	@Override
	public void startScheduledService() {
		dataStore.getAll().forEach(this::download);
		super.startScheduledService();
		sourceEvent.send("session-start");
	}

	@Override
	protected void checkDownloadList() {
		if(listPaused){
			List<ItemMetaData> pause = new LinkedList<>();
			for (ItemMetaData metaData : downloadingList) {
				moveToPauseList(metaData);
				pause.add(metaData);
			}
			pauseEvent(pause);
		} else {
			super.checkDownloadList();
		}
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

	private String toJsonItem(ItemMetaData metaData){
		return Utils.toJson(new WebItem(metaData));
	}

	private String toJsonItemsList(Stream<ItemMetaData> itemStream){
		return Utils.toJson(itemStream.map(WebItem::new).collect(Collectors.toList()));
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

	private Optional<ItemMetaData> searchById(Queue<ItemMetaData> queue, Integer id) {
		return queue.stream().filter(item -> item.getItem().getId().equals(id)).findAny();
	}

	private ItemMetaData find(Integer id) {
		Optional<ItemMetaData> optional;
		ItemMetaData metaData = null;
		for (Queue<ItemMetaData> queue : queues) {
			optional = searchById(queue, id);
			if (optional.isPresent()) {
				metaData = optional.get();
				break;
			}
		}
		return metaData;
	}

	private ItemMetaData findAndPause(Integer id) {
		Optional<ItemMetaData> optional;
		ItemMetaData metaData = null;
		for (Queue<ItemMetaData> queue : queues) {
			optional = searchById(queue, id);
			if (optional.isPresent()) {
				metaData = optional.get();
				queue.remove(metaData);
				metaData.pause();
				metaData.systemFlush();
				break;
			}
		}
		return metaData;
	}

	public boolean deleteAndRemoveItem(Integer id) {
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

	public boolean pauseItem(Integer id) {
		ItemMetaData metaData = findAndPause(id);
		return Objects.nonNull(metaData);
	}

	public boolean startItem(Integer id) {
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

	public Integer download(String url) {
		return this.download(url, Collections.emptyMap());
	}

	public Integer download(String url, Map<String, List<String>> headers) {
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

	public Integer downloadMetaLink(String[] urls) {
		return this.downloadMetaLink(urls, Collections.emptyMap());
	}

	public Integer downloadMetaLink(String[] urls, Map<String, List<String>> headers) {
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
