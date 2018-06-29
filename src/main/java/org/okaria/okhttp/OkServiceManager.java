package org.okaria.okhttp;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.log.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.manager.ItemResolver;
import org.okaria.range.RangeInfo;
import org.okaria.range.RangeUtils;

public class OkServiceManager implements RangeUtils {

	public final static int MAX_ACTIVE_DOWNLOAD_POOL = 5;
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 5;

	ScheduledExecutorService cheduledService;

	Map<File, Item> itemsMap;

	OkConfig config;
	OkClient client;
	ItemResolver resolver;

	// ReportMonitor monitor;

	SpeedReportMonitor monitor;
	boolean down = false;

	public OkServiceManager(Proxy.Type type, String proxyHost, int port) {
		monitor = new SpeedReportMonitor(); // new ReportMonitor();
		config = new OkConfig(CookieJars.CookieJarMap, type, proxyHost, port);
		client = new OkClient(config);
		resolver = new ItemResolver(client.getHttpClient());

		cheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		itemsMap = new LinkedHashMap<File, Item>();

	}
	// public void startMonitor() {
	// if (down)
	// monitor.start();
	// }

	public OkClient getOkClient() {
		return client;
	}

	// private void add(String pathname, Item item) {
	// addItem(new File(pathname), item);
	// }

	private void addItem(File key, Item item) {
		itemsMap.put(key, item);
	}

	// private void clearItems() {
	// itemsMap.clear();
	// }

	private Item getItem(File key) {
		return itemsMap.get(key);
	}

	private void saveItemToDisk() {
		for (File file : itemsMap.keySet()) {
			Item item = getItem(file);
			Utils.toJsonFile(file, item);
		}
	}

	private void checkFinishState() {
		// System.out.println("checkFinishState");
		List<File> toRemove = new ArrayList<>();
		for (File file : itemsMap.keySet()) {

			Item item = getItem(file);
			// System.out.println(item.getFilename());
			RangeInfo info = item.getRangeInfo();
			if (info.isFinish()) {
				toRemove.add(file);
				continue;
			}
			for (int index = 0; index < info.getRangeCount(); index++) {
				if (info.isFinish(index)) {
					// System.out.println( "#"+ i + " is finish " );
					boolean updated = info.updateIndexFromMaxRange(index);
					if (updated) {
						Log.info(getClass(), "Item Update", item.toString());
						client.downloadPart(item, index, monitor);
					}

				}
			}
		}
		toRemove.forEach(file -> {
			Item item = itemsMap.remove(file);
			Utils.toJsonFile(file, item);
		});
		if (itemsMap.isEmpty()) {
			beforeExit();
			System.exit(0);
		}
	}

	private void beforeExit() {
		cheduledService.shutdown();
		printReport();
	}

	
	
	
	private void printReport() {
		System.out.print(monitor.getMointorPrintMessage());
//		String message = monitor.getMointorPrintMessageln();
		//Log.info(getClass(), "Moinitor Report", monitor.getMointorPrintMessageln());
//		System.out.print(message);
		
//		Log.print(monitor.getMointorPrintMessage());
	}

	public void startScheduledService() {
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::printReport, 1, 1, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveItemToDisk, 2, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::checkFinishState, 3, SCHEDULE_TIME, TimeUnit.SECONDS);

	}


	public void downloadURL(String url) {
		downloadURL(url, new HashMap<>());
	}
	public void downloadURL(String url, Map<String, String> headers) {
		Item item = resolver.urlToItem(url, headers);
		if (item.getRangeInfo().isUnknowLength())
			resolver.resolveItem(item);
		Log.info(getClass(), "start download item", item.toString());
		
		// monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
		addItem(new File(R.getConfigFile(item.getFilename() + ".json")), item);

		long rem = item.getRangeInfo().getRengesLength();
		monitor.addLength(rem);
		client.download(item, monitor);
		down = true;
	}

	public void downloadFromFileAsList(String filename) {
		List<Item> items = resolver.readUrlListFromFile(filename);
		// resolver.resolveItem(items);
		items.forEach(item -> {
			if (item.getRangeInfo().isUnknowLength())
				resolver.resolveItem(item);
			Log.info(getClass(), "start download item", item.toString());
		});
		items.forEach(item -> {
			// monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
			addItem(new File(R.getConfigFile(item.getFilename() + ".json")), item);

			long rem = item.getRangeInfo().getRengesLength();
			monitor.addLength(rem);
		});
		items.forEach(item -> client.download(item, monitor));
		down = true;
	}

	public void downloadItemList(List<String> urls) {
		List<Item> items = resolveUrl2Items(urls);
		for (Item item : items) {
			// if(item.isFinished()) continue;
			client.download(item, monitor);
		}
		down = true;

	}

	public List<Item> resolveUrl2Items(List<String> urls) {
		List<Item> items = new ArrayList<Item>();
		ItemResolver resolver = new ItemResolver(client.getHttpClient());

		for (String downloadUrl : urls) {
			String filename = Utils.Filename(downloadUrl);
			Item item = Utils.fromJson(R.getConfigFile(filename + ".json"), Item.class);
			if (item == null) {
				item = resolver.resolveUrl(downloadUrl);
			} else {
				item.getRangeInfo().avoidMissedBytes();
				item.getRangeInfo().checkRanges();
			}

			if (item.getRangeInfo().isFinish()) {
				Log.info(getClass(), "try resolve item", item.toString());
				if (item.getRangeInfo().isFinish()) {
					Log.info(getClass(), "item complete", item.isFinish()+"");
					continue;
				}
				items.add(item);
				// monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
				addItem(new File(R.getConfigFile(item.getFilename() + ".json")), item);
				long rem = item.getRangeInfo().getRengesLength();
				monitor.addLength(rem);
			}

		}
		return items;
	}

}
