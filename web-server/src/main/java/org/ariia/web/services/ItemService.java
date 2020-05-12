package org.ariia.web.services;

import java.io.FileInputStream;
import java.util.List;

import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.mvc.resource.StreamHandler;
import org.ariia.web.app.WebServiceManager;

import com.sun.net.httpserver.HttpExchange;

public class ItemService implements StreamHandler {
	
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
	
	public void downloadItem(String id, HttpExchange exchange) {
		Item item =  dataStore.findById(id);
		try {
			if(item.getState().isComplete()) {
				FileInputStream stream = new FileInputStream(item.path());
				handelStreamAndSetFileName(exchange, item.getFilename(), stream);
			} else {
				// HTTP_UNAVAILABLE = 503
				exchange.sendResponseHeaders(503, -1);
				exchange.close();
			}
		} catch (Exception e) {
			Log.error(getClass(), "Download Item", e.getMessage());
		}
	}
	

}
