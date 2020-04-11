package org.ariia.web.controller;

import org.ariia.items.Item;
import org.ariia.mvc.annoutation.GetRequest;
import org.ariia.mvc.annoutation.PostRequest;
import org.ariia.mvc.annoutation.RequestBody;
import org.ariia.mvc.annoutation.RequestParam;
import org.ariia.mvc.annoutation.RestContext;
import org.ariia.web.services.ItemService;

@RestContext("/item")
public class ItemController {
	
	private final ItemService itemService;
	
	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}
	
	@GetRequest(path = "/{id}")
	public Item getItem(@RequestParam("id") String id) {
		return itemService.get(id);
	}
	
	@PostRequest(path = "/create")
	public String createItem(@RequestBody Item item) {
		return itemService.create(item);
	}
	
	@PostRequest(path = "/create")
	public String createItem(@RequestBody String url) {
		return itemService.create(url);
	}
	

}
