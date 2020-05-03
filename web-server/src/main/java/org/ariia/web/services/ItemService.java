package org.ariia.web.services;

import java.util.List;

import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.web.app.WebServiceManager;

public class ItemService {
	
	private WebServiceManager serviceManager;
	private DataStore<Item> dataStore;
	
	public ItemService(WebServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		this.dataStore = this.serviceManager.getDataStore();
	}

	public Item get(String id) {
		return dataStore.findById(id);
	}

	public String create(Item item) {
		this.serviceManager.download(item);
		return item.getId();
	}
	
	public String create(String url) {
		return this.serviceManager.download(url);
	}

	public List<Item> getItems() {
		return dataStore.getAll();
	}
	
	
	public boolean delete(String id) {
		return this.serviceManager.deleteAndRemoveItem(id);
	}
	
	public boolean pause(String id) {
		return this.serviceManager.pauseItem(id);
	}
	
	public boolean start(String id) {
		return this.serviceManager.startItem(id);
	}
	

}
