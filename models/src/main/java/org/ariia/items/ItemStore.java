package org.ariia.items;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ariia.util.R;
import org.ariia.util.Utils;

public class ItemStore implements DataStore<Item> {

	private List<Item> items;
	private String storePath;
	
	public ItemStore() {
		this(R.CachePath + "item-store"+ R.sprtr );
	}
	public ItemStore(String storePath) {
		this.storePath = Objects.requireNonNull(storePath);
		items = new ArrayList<>();
		refreshStore();
	}
			
	public void refreshStore() {
		items.clear();
		R.MK_DIRS(storePath);
		File dir = new File( storePath );
		File[] files = dir.listFiles();
		for (File file : files) {
			if(file.getName().endsWith(".json~")) {
				file.delete();
				continue;
			}
			items.add(Utils.fromJson(file, Item.class));
		}
	}
	
	
	
	@Override
	public Item findByUrl(String url) {
		Optional<Item> findItem =  items.stream()
			.filter(item -> item.getUrl().equals(url))
			.findFirst();
		return findItem.orElse(null);
	}

	@Override
	public Item findByFileName(String fileName) {
		Optional<Item> findItem =  items.stream()
				.filter(item -> item.getFilename().equals(fileName))
				.findFirst();
			return findItem.orElse(null);
	}

	@Override
	public Stream<Item> streamByUrlAndDirectory(String url, String dir) {
		return items.stream()
				.filter(item -> item.getUrl().equals(url))
				.filter(item -> item.getSaveDirectory().equals(dir));
	}

	@Override
	public Item findById(Integer id) {
		Optional<Item> findItem =  items.stream()
				.filter(item -> Objects.equals(item.getId(), id))
				.findFirst();
			return findItem.orElse(null);
	}

	@Override
	public void save(Item item) {
		String cacheFile = getCacheFile(item);
		Utils.toJsonFile(cacheFile, item);
	}

	private String getCacheFile(Item item) {
		return storePath + item.getFilename() + '-' + item.getUuid() + ".json";
	}

	@Override
	public void add(Item item) {
		if (!items.contains(item)) {
			this.items.add(item);
		}
	}
	
	@Override
	public Integer getId(String url) {
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
	
	@Override
	public boolean remove(Item item) {
		String cacheFile = getCacheFile(item);
		return new File(cacheFile).delete() && items.remove(item);
	}

}
