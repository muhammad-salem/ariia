package org.ariia.web.services;

import java.util.List;

import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.DataStore;
import org.ariia.items.Item;

public class ItemService {
	
	private ServiceManager serviceManager;
	private DataStore<Item> dataStore;
	
	public ItemService(ServiceManager serviceManager) {
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
	
	

}
