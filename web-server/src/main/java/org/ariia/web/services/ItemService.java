package org.ariia.web.services;

import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.Builder;
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
		return this.dataStore.getId(item.getUrl());
	}
	
	public String create(String url) {
		Builder builder = new Builder(url);
		return create(builder.build());
	}
	
	

}
