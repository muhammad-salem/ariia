package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.ariia.util.R;
import org.ariia.util.Utils;

public class ItemStore implements DataStore<Item> {

	private List<Item> items;
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
		Stream<Item> stream = streamByUrlAndDirectory(url, dir);
		long count = stream.count();
		if (count == 1) {
			return stream.findAny().get();
		}
		return null;
	}

}
