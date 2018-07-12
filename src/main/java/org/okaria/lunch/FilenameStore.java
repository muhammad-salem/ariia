package org.okaria.lunch;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item;

public class FilenameStore{

	String storename = "store.json";
	Map<String, String> store ;
	
	public FilenameStore() {
		store = new LinkedHashMap<>();
	}
	
	public FilenameStore(String storenam) {
		this.storename = storenam;
		this.store = new LinkedHashMap<>();
	}
	
	public void setStorename(String storename) {
		this.storename = storename;
	}
	
	public String getStorename() {
		return storename;
	}
	
	public String json() {
		return Utils.toJson(store);
	}
	
	public void jsonFile() {
		
		Utils.toJsonFile(R.getConfigPath( storename ), this);
	}
	
	public static FilenameStore LoadStore() {
		return LoadStore("store.json");
	}
	public static FilenameStore LoadStore(String storename) {
		try {
			return Utils.fromJson(R.getConfigPath( storename ), FilenameStore.class);
		} catch (Exception e) {
			return InitStore();
		}
	}
	
	public static FilenameStore DEFAULT = EmptyStore();

	protected static FilenameStore EmptyStore() {
		FilenameStore store = new FilenameStore();
		return store;
	}
	protected static FilenameStore InitStore() {
		File dir = new File( R.getConfigDirectory());
		File[] files = dir.listFiles();
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				long c = System.currentTimeMillis();
				long m1 = (c - o1.lastModified());
				long m2 = (c - o2.lastModified());
				if(m1 == m2)
				return 0;
				if(m1 > m2)
					return 1;
				return -1;
			}
			 
		});
		FilenameStore store = new FilenameStore();
		for (File file : files) {
			
			Item item = Utils.fromJson(file, Item.class);
			if(item == null) continue;
			
			store.put(item.url(), item.getFilename());
			//System.out.println(file.getPath());
		}
		
		return store;
	}

	
	public int size() {
		return store.size();
	}

	
	public boolean isEmpty() {
		return store.isEmpty();
	}

	
	public boolean containsKey(String key) {
		return store.containsKey(key);
	}

	
	public boolean containsValue(String value) {
		return store.containsValue(value);
	}

	
	public String get(String key) {
		String value = store.get(key);
		if(value == null) {
			value = Utils.Filename(key);
			store.put( key, value);
		}
		return store.get(key);
	}
	
	public String getDotJson(String key) {
		return get(key) + ".json";
	}

	
	public String put(String key, String value) {
		return store.put(key, value);
	}

	
	public String remove(String key) {
		return store.remove(key);
	}

	
	public void putAll(Map<? extends String, ? extends String> m) {
		store.putAll(m);
	}

	
	public void clear() {
		store.clear();
	}

	
	public Set<String> keySet() {
		return store.keySet();
	}

	
	public Collection<String> values() {
		return store.values();
	}

	
	public Set<Entry<String, String>> entrySet() {
		return store.entrySet();
	}

}
