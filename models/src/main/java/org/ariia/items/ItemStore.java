package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ariia.util.R;
import org.ariia.util.Utils;

public class ItemStore implements DataStore<Item> {

	private List<Item> items;
	private String storePath;
	
	public ItemStore() {
		this(R.CachePath + R.sprtr + "item-store"+ R.sprtr );
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
			if(file.getName().endsWith(".json~")) {
				file.delete();
				continue;
			}
			items.add(Utils.fromJson(file, Item.class));
//			Item item = Utils.fromJson(file, Item.class);
//			if(Objects.nonNull(item)) {
//				if (item.isFinish()) {
//					// clean cache files
//					file.delete();
//				} else {
//					items.add(item);
//				}
//			}
		}
	}
	
	
	
	@Override
	public Item findByUrl(String url) {
		Optional<Item> findItem =  items.stream()
			.filter(item -> {return item.getUrl().equals(url); })
			.findFirst();
		return findItem.isPresent() ? findItem.get() : null;
	}

	@Override
	public Item findByFileName(String fileName) {
		Optional<Item> findItem =  items.stream()
				.filter(item -> {return item.getFilename().equals(fileName); })
				.findFirst();
			return findItem.isPresent() ? findItem.get() : null;
	}

	@Override
	public Stream<Item> streamByUrlAndDirectory(String url, String dir) {
		return items.stream()
				.filter(item -> {return item.getUrl().equals(url); })
				.filter(item -> {return item.getSaveDirectory().equals(dir); });
	}

	@Override
	public Item findById(String id) {
		Optional<Item> findItem =  items.stream()
				.filter(item -> {return item.getId().equals(id); })
				.findFirst();
			return findItem.isPresent() ? findItem.get() : null;
	}

	@Override
	public void save(Item item) {
		String cacheFile = storePath + item.getId() + '-' + item.getFilename() + ".json";
		Utils.toJsonFile(cacheFile, item);
	}

	@Override
	public void add(Item item) {
		if (!items.contains(item)) {
			this.items.add(item);
		}
	}
	
	@Override
	public String getId(String url) {
		return findByUrl(url).getId();
	}
	@Override
	public Item findByUrlAndSaveDirectory(String url, String dir) {
		List<Item> items = streamByUrlAndDirectory(url, dir).collect(Collectors.toList());
		if (items.size() == 1) {
			return items.get(0);
		}
		return null;
	}
	@Override
	public List<Item> getAll() {
		return items;
	}

}
