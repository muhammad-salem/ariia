package org.okaria.okhttp;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.log.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.lunch.Argument;
import org.okaria.lunch.FilenameStore;
import org.okaria.manager.Item;
import org.okaria.manager.ItemIndictor;
import org.okaria.manager.MetalinkItem;
import org.okaria.range.RangeInfo;
import org.okaria.range.RangeInfoMonitor;
import org.okaria.range.RangeUtils;

public class OkServiceManager implements RangeUtils {

	public static int MAX_ACTIVE_DOWNLOAD_POOL = 4;
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 5;

	ScheduledExecutorService cheduledService;

//	Map<File, Item> items;
	Queue<ItemIndictor> wattingList;
	Queue<ItemIndictor> downloadingList;

	OkConfig config;
	OkClient client;
	FilenameStore store;
	RangeInfoMonitor monitor;

	public OkServiceManager(Proxy.Type type, String proxyHost, int port) {
		this(new Proxy(type, new InetSocketAddress(proxyHost, port)));
	}

	
	public OkServiceManager(Proxy proxy) {
		this(proxy, CookieJars.CookieJarMap);
	}

	public OkServiceManager(Proxy proxy, CookieJars cookieJars) {
		monitor = new RangeInfoMonitor(); // new ReportMonitor();
		config = new OkConfig(cookieJars, proxy);
		
		store = FilenameStore.DEFAULT; //FilenameStore.LoadStore("okstore.json");
		if (store == null)
			store = new FilenameStore();
		client = new OkClient(config, store);
		
		wattingList 	= new LinkedList<>();//= new LinkedBlockingQueue<>();
		downloadingList = new LinkedList<>();
		//items = new LinkedHashMap<File, Item>();

		cheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			saveDownloadingItemToDisk();
			saveWattingItemToDisk();
			System.out.println("\n\n\n\n\n");
		}));

	}

	

	public OkClient getOkClient() {
		return client;
	}

	private void addItem(Item item, File cacheFile) {
		ItemIndictor indictor = new ItemIndictor(item, cacheFile);
		wattingList.add(indictor);
	}

	private void saveDownloadingItemToDisk() {
		for (ItemIndictor indictor : downloadingList) {
			indictor.saveItem2CacheFile();
		}
	}
	
	private void saveWattingItemToDisk() {
		for (ItemIndictor indictor : wattingList) {
			indictor.saveItem2CacheFile();
		}
	}
	
	private void checkdownloadList() {
		if(downloadingList.size() < MAX_ACTIVE_DOWNLOAD_POOL) {
			while (downloadingList.size() < MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
				downloadingList.add(wattingList.poll());
			}
		}
		
		
		List<ItemIndictor> removeList = new ArrayList<>();
		for (ItemIndictor indictor : downloadingList) {
			Item item = indictor.getItem();
			RangeInfo info = item.getRangeInfo();
			if (info.isFinish()) {
				removeList.add(indictor);
				continue;
			}else if(! indictor.isDownloading()) {
				indictor.download(client, monitor);
			}
			indictor.replaceDoneFuturesFromMax(client, monitor);
			
		}
		
		downloadingList.removeAll(removeList);
		
		if (downloadingList.isEmpty() && wattingList.isEmpty()) {
			beforeExit();
			System.exit(0);
		}
	}

	private void beforeExit() {
		cheduledService.shutdown();
		// try if you can :P
		printReport();
	}

	private void printReport() {
		System.out.print(monitor.getMointorPrintMessage());
	}
	

	public void startScheduledService() {
		
		cheduledService.execute(this::saveWattingItemToDisk);
		
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::printReport, 1, 1, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveDownloadingItemToDisk, 2, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::checkdownloadList, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
		
	}

	
	public void startScheduledService(boolean printSpeedReport) {
		// for each 2 second
		if(printSpeedReport)
			cheduledService.scheduleWithFixedDelay(this::printReport, 1, 1, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveDownloadingItemToDisk, 2, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::checkdownloadList, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
	}

	
	public List<Item.Builder> readListFile(String inputfile) {
		List<String> urls = OkUtils.readLines(inputfile);
		
		Iterator<String> iterator = urls.iterator();
		List<Item.Builder> builders = new LinkedList<>();
		Item.Builder builder = null;
		Map<String, String> headers = null;
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			
			if (string.startsWith("#")) {
				if (builder != null) {
					builders.add(builder);
					builder = null;
					headers = null;
				}
				iterator.remove();
			} else if (string.startsWith("http")) {
				if (builder != null) {
					builders.add(builder);
					builder = null;
					headers = null;
				}
				builder = new Item.Builder();
				headers = new LinkedHashMap<>();
				builder.url(string);
				builder.headers(headers);
			} else if (string.startsWith("\t")) {
				if (headers != null) {
					int index = string.indexOf(": ");
					headers.put(string.substring(1, index), string.substring(index + 2));
				}

			}
		}
		if (builder != null)
			builders.add(builder);

		return builders;
	}

	public MetalinkItem.Builder readMetaLinkXML(String metaLinkFile) {
		return null;
	}
	public MetalinkItem.Builder readMetaLinkText(String metaLinkFile) {
		List<String> urls = OkUtils.readLines(metaLinkFile);
		Iterator<String> iterator = urls.iterator();
		MetalinkItem.Builder builder = new MetalinkItem.Builder();
		Map<String, String> headers = new LinkedHashMap<>();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			if (string.startsWith("#")) {
				iterator.remove();
			} else if (string.startsWith("http")) {
				builder.addUrl(string);
			} else if (string.startsWith("\t")) {
				int index = string.indexOf(": ");
				headers.put(string.substring(1, index), string.substring(index + 2));
			}
		}
		builder.headers(headers);
		return builder;
	}
	
	

	
//	public void download(String fileInput, ) {
//		List<Item> items = resolver.readUrlListFromFile(filename);
//		// resolver.resolveItem(items);
//		items.forEach(item -> {
//			if (item.getRangeInfo().isUnknowLength())
//				resolver.resolveItem(item);
//			Log.info(getClass(), "start download", item.toString());
//		});
//		items.forEach(item -> {
//			// monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
//			addItem(new File(R.getConfigPath(store.getDotJson(item.url()))), item);
//			monitor.add(item.getRangeInfo());
//		});
//		items.forEach(item -> client.download(item, monitor));
//	}
	
	

	public void download(Argument arguments) {
		if(arguments.isUrl()) {
			downloadUrl(arguments);
		}
		else if(arguments.isInputFile()) {
			downloadInputFile(arguments);
		}
		else if(arguments.isMetaLink()) {
			downloadMetalink(arguments);
		}
	}
	

	public void downloadUrl(Argument arguments) {
		Item.Builder builder = new Item.Builder();
		builder.url(arguments.getUrl());
		configBuilder(arguments, builder);
		if(arguments.isFileName()) 
			builder.filename(arguments.getFileName());
		Item item = buildItem(builder);
		addItem2WattingList(item);
//		if(addItem(item) )
//			startItemDownload(item);
	}
	

	public void downloadInputFile(Argument arguments) {
		List<Item.Builder> builders = readListFile(arguments.getInputFile());
//		List<Item> items = new LinkedList<>();
		for (Item.Builder builder : builders) {
			configBuilder(arguments, builder);
			Item item = buildItem(builder);
			addItem2WattingList(item);
//			if(addItem(item) )
//				items.add(item);
		}
//		for (Item item : items) {
//			startItemDownload(item);
//		}
	}
	
	public void downloadMetalink(Argument arguments) {
		MetalinkItem.Builder builder = null;
		if(arguments.getMetaLinkFile().contains(".xml")) {
			builder = readMetaLinkXML(arguments.getMetaLinkFile());
		}else {
			builder = readMetaLinkText(arguments.getMetaLinkFile());
		}
		configBuilder(arguments, builder);
		MetalinkItem item = buildItem(builder);
		addItem2WattingList(item);
//		if(addItem(item) )
//			startItemDownload(item);
	}

	/**
	 * @param arguments
	 * @param builder
	 */
	protected void configBuilder(Argument arguments, Item.Builder builder) {
		if(arguments.isReferer()) 
			builder.referer(arguments.getReferer());
		
		builder.addHeaders(arguments.getHeaders());
		builder.addCookies(arguments.getAllCookie());
		if(arguments.isUserAgent())
			builder.useragent(arguments.getUserAgent());
	}
	
	
	protected Item buildItem(Item.Builder builder) {
		String filename = store.getDotJson(builder.url());
		Item item = Utils.fromJson(R.getConfigPath(filename), Item.class);
		if (item == null) {
			item = client.resolveItem(builder.build());
		} else {
			item.getRangeInfo().avoidMissedBytes();
			item.getRangeInfo().checkRanges();
		}
		return item;
	}
	
	protected MetalinkItem buildItem(MetalinkItem.Builder builder) {
		String filename = store.getDotJson(builder.url());
		MetalinkItem item = Utils.fromJson(R.getConfigPath(filename), MetalinkItem.class);
		if (item == null) {
			item = (MetalinkItem) client.resolveItem(builder.build());
		} else {
			item.getRangeInfo().avoidMissedBytes();
			item.getRangeInfo().checkRanges();
		}
		return item;
	}
	
	
	public boolean addItem2WattingList(Item item) {
		if( item.isFinish() ) {
			Log.info(getClass(), "Complete Download", item.liteString());
			return false;
		}
		Log.info(getClass(), "add download item", item.toString());
		addItem(item, R.getConfigFile(store.getDotJson(item.url())));
		monitor.add(item.getRangeInfo());
		return true;
	}

	/**
	 * @param item
	 */
//	protected void startItemDownload(Item item) {
//		client.download(item, monitor);
//	}

}
