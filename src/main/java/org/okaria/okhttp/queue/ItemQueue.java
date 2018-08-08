package org.okaria.okhttp.queue;

import java.util.List;
import java.util.Queue;

import org.okaria.manager.Item;

public class ItemQueue {
	
	String name;
	int capacity = 30;
	Queue<Item> items;
	int maxDownloading = 3;
	List<Item> downloadItems;
	
}
