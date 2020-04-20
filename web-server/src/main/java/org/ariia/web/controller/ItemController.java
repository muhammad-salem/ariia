package org.ariia.web.controller;

import java.util.List;

import org.ariia.items.Item;
import org.ariia.mvc.annotation.PathVariable;
import org.ariia.mvc.annotation.RequestBody;
import org.ariia.mvc.annotation.RestController;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.services.ItemService;

@RestController("/items")
public class ItemController {
	
	private final ItemService itemService;
	
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@GetRequest(path = "/all")
	public List<Item> items() {
		return itemService.getItems();
	}
	
	@GetRequest(path = "/{id}")
	public Item getItem(@PathVariable("id") String id) {
		return itemService.get(id);
	}
	
	@PostRequest(path = "/createUrl")
	public String createItem(@RequestBody String url) {
		return itemService.create(url);
	}
	
	
	@PostRequest(path = "/createItem")
	public String createItem(@RequestBody Item item) {
		return itemService.create(item);
	}
	

}
