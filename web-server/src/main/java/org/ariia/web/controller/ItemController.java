package org.ariia.web.controller;

import java.util.List;

import org.ariia.items.Item;
import org.ariia.mvc.annotation.DoExchange;
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
		return itemService.getItems();
	}
	
	@GetRequest(path = "/info/{id}")
	public Item getItem(@PathVariable("id") String id) {
		return itemService.get(id);
	}
	
	@PostRequest(path = "/create/url")
	public String createItem(@RequestBody String url) {
		return itemService.create(url);
	}
	
	
	@PostRequest(path = "/create/item")
	public String createItem(@RequestBody Item item) {
		return itemService.create(item);
	}
	
	@DeleteRequest(path = "/delete/{id}")
	public boolean delete(@PathVariable("id") String id) {
		return this.itemService.delete(id);
	}
	
	@PostRequest(path = "/pause/{id}")
	public boolean pause(@PathVariable("id") String id) {
		return this.itemService.pause(id);
	}
	
	@PostRequest(path = "/start/{id}")
	public boolean start(@PathVariable("id") String id) {
		return this.itemService.start(id);
	}
	
	
	@GetRequest(path = "/download/{id}")
	@DoExchange
	public void downloadItem(@PathVariable("id") String id, HttpExchange exchange) {
		this.itemService.downloadItem(id, exchange);
	}

}
