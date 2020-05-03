package org.ariia.items;

import java.util.List;
import java.util.stream.Stream;

public interface DataStore<T extends Item> {

	T findByUrl(String url);

	T findByFileName(String fileName);

	T findByUrlAndSaveDirectory(String url, String dir);
	
	Stream<T> streamByUrlAndDirectory(String url, String dir);
	
	T findById(String id);
	
	String getId(String url);
	
	void save(T item);
	void add(T item);
	
	List<T> getAll();
	
	boolean remove(T item);

}