package org.aria.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import org.aria.util.R;
import org.aria.util.Utils;

public class ItemStore {

	private ArrayList<Item> items;
	private String storePath;
	
	public ItemStore() {
		this(R.getConfigDirectory());
	}
	public ItemStore(String storePath) {
		items = new ArrayList<>();
		this.storePath = storePath;
		refreshStore();
	}
			
	public void refreshStore() {
		items.clear();
		File dir = new File( storePath );
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
					items.add(item);
				}
			}
		}
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
	
	public void toJsonFile(Item item) {
		String cacheFile = storePath + item.getFilename() + ".json";
		Utils.toJsonFile(cacheFile, item);
	}

}
