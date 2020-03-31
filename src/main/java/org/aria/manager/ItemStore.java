package org.aria.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.aria.util.R;
import org.aria.util.Utils;

public final class ItemStore {
	
	public static ItemStore CreateAndInitStore() {
		ItemStore store = new ItemStore();
		InitStore(store, R.getConfigDirectory());
		return store;
	}
	
	public static void InitStore(ItemStore store) {
		InitStore(store, R.getConfigDirectory());
	}
			
	public static final void InitStore(ItemStore store, String folder) {
		File dir = new File( folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if(file.getName().contains(".json~")) {
				file.delete();
			}
			Item item = Utils.fromJson(file, Item.class);
			if(Objects.nonNull(item)) {
				if (item.isFinish()) {
					// clean cache files
					file.delete();
				} else {
					store.items.add(item);
				}
			}
			
			
		}
	}

	
	private List<Item> items;
	
	private ItemStore() {
		items = new LinkedList<>();
	}
	
	public Item searchByUrl(String url) {
		for (Item item : items) {
			if(url.equals(item.getUrl())) return item;
		}
		return null;
	}
	
	public Item searchByFileName(String fileName) {
		for (Item item : items) {
			if(item.getFilename().equals(fileName)) return item;
		}
		return null;
	}
	
	public Stream<Item> searchByUrlDir(String url, String dir) {
		return items.stream().filter(item -> { 
			return item.getUrl().equals(url) 
					&& item.getSaveDir().equals(dir);
			});
	}
	
	
	

}
