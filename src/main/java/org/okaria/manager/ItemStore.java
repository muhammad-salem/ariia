package org.okaria.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.okaria.util.R;
import org.okaria.util.Utils;

public class ItemStore {
	
	
	
	public static final ItemStore CreateAndInitStore() {
		ItemStore store = new ItemStore();
		InitStore(store, R.getConfigDirectory());
		return store;
	}
	
	public static final void InitStore(ItemStore store) {
		InitStore(store, R.getConfigDirectory());
	}
			
	public static final void InitStore(ItemStore store, String folder) {
		File dir = new File( folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if(file.getName().contains(".json~")) continue;
			Item item = Utils.fromJson(file, Item.class);
			if(item == null) continue;
			store.items.add(item);
			
		}
	}

	
	List<Item> items;
	
	private ItemStore() {
		items = new LinkedList<>();
	}
	
	public Item searchByUrl(String url) {
		for (Item item : items) {
			if(item.getUrl().equals(url)) return item;
		}
		return null;
	}
	
	public Item searchByFileName(String fileName) {
		for (Item item : items) {
			if(item.getFilename().equals(fileName)) return item;
		}
		return null;
	}
	
	
	

}
