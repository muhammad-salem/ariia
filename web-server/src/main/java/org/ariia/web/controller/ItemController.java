package org.ariia.web.controller;

import java.util.List;

import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.mvc.annotation.DoExchange;
import org.ariia.mvc.annotation.HeaderValue;
import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.DeleteRequest;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.services.ItemService;

import com.sun.net.httpserver.HttpExchange;

@RestController("/items")
public class ItemController {
	
	private final ItemService itemService;
	
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@GetRequest(path = "")
	public List<Item> items() {
		Log.trace(getClass(), "GET ALL Items");
		return itemService.getItems();
	}
	
	@GetRequest(path = "/info/{id}")
	public Item getItem(@PathVariable("id") String id) {
		Log.trace(getClass(), "GET Item Info", "id: " + id);
		return itemService.get(id);
	}
	
	@PostRequest(path = "/create/url")
	public String createItem(@RequestBody String url) {
		Log.trace(getClass(), "Create new Item", "url: " + url);
		return itemService.create(url);
	}
	
	
	@PostRequest(path = "/create/item")
	public String createItem(@RequestBody Item item) {
		Log.trace(getClass(), "Create new Item", "item: " + item);
		return itemService.create(item);
	}
	
	@DeleteRequest(path = "/delete/{id}")
	public boolean delete(@PathVariable("id") String id) {
		Log.trace(getClass(), "Delete Item Request", "id: " + id);
		return this.itemService.delete(id);
	}
	
	@PostRequest(path = "/pause/{id}")
	public boolean pause(@PathVariable("id") String id) {
		Log.trace(getClass(), "Pause Item", "id: " + id);
		return this.itemService.pause(id);
	}
	
	@PostRequest(path = "/start/{id}")
	public boolean start(@PathVariable("id") String id) {
		Log.trace(getClass(), "Start Download Item", "id: " + id);
		return this.itemService.start(id);
	}
	
	
	@GetRequest(path = "/download/{id}/{filename}")
	@DoExchange
	public void downloadItem(
			@PathVariable("id") String id,
			@PathVariable("filename") String filename,
			HttpExchange exchange) {
		Log.trace(getClass(), "Download Request", "id: " + id + " filename: " + filename);
		this.itemService.downloadItem(id, exchange);
	}
	
	@GetRequest(path = "/download/{id}/{filename}", headers = {"Range"})
	@DoExchange
	public void downloadItemParts (
			@PathVariable("id") String id,
			@PathVariable("filename") String filename,
			@HeaderValue("Range") String range, 
			HttpExchange exchange) {
		Log.trace(getClass(), "Download Request", "id: " + id + "\nfilename: " + filename + "\nRange: " + range);
		this.itemService.downloadItemParts(id, exchange, range);
	}

}
