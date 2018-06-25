package org.okaria.okhttp;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.okaria.R;
import org.okaria.Utils;
import org.okaria.manager.Item;
import org.okaria.manager.ItemResolver;
import org.okaria.range.RangeInfo;
import org.okaria.range.RangeUtils;
import org.terminal.Ansi;

public class OkServiceManager implements RangeUtils {

	public final static int MAX_ACTIVE_DOWNLOAD_POOL = 5;
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 5;

	
	ScheduledExecutorService cheduledService;
	
	Map<File, Item> itemsMap ;
	
	OkConfig config;
	OkClient client;
	ItemResolver resolver;
	
//	ReportMonitor monitor;
	
	SpeedReportMonitor monitor;
	boolean down = false;
		
	public OkServiceManager(Proxy.Type type, String proxyHost, int port) {
		monitor =  new  SpeedReportMonitor(); //new ReportMonitor();
		config = new OkConfig(CookieJars.CookieJarMap, type, proxyHost, port);
		client = new OkClient(config);
		resolver = new ItemResolver(client.getHttpClient());
		
		cheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		itemsMap = new LinkedHashMap<File, Item>();
		
	}
//	public void startMonitor() {
//		if (down)
//			monitor.start();
//	}

	public OkClient getOkClient() {
		return client;
	}

//	private void add(String pathname, Item item) {
//		addItem(new File(pathname), item);
//	}

	private void addItem(File key, Item item) {
		itemsMap.put(key, item);
	}

//	private void clearItems() {
//		itemsMap.clear();
//	}

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
//		System.out.println("checkFinishState");
		List<File> toRemove = new ArrayList<>();
		for (File file : itemsMap.keySet()) {
			
			Item item = getItem(file);
//			System.out.println(item.getFilename());
			RangeInfo info = item.getRangeInfo();
			if(info.isFinish()) {
				toRemove.add(file);
				continue;
			}
			for (int index = 0; index < info.getRangeCount(); index++) {
				if (info.isFinish(index)) {
//					System.out.println( "#"+ i + " is finish " );
					boolean updated = info.updateIndexFromMaxRange(index);
					if(updated) {
						System.out.println(item);
						client.downloadPart(item, index, monitor);
						//client.downloadPart(item.getUpdateHttpUrl(), info.getIndex(index), item.getSavepathFile(), monitor);
					}
					
				}
//				else {
//					System.out.println("no");
//				}
			}
		}
		toRemove.forEach(file-> {
			Item item = itemsMap.remove(file);
			Utils.toJsonFile(file, item);
		});
		if(itemsMap.isEmpty()) {
			beforeExit();
			System.exit(0);
		}
	}
	
	private void beforeExit() {
		//try {TimeUnit.SECONDS.sleep(2);} catch (Exception e) {}
		cheduledService.shutdown();
		//saveItemToDisk();
		printReport();
	}
	
	private void printReport() {
		StringBuilder builder = new StringBuilder();
		builder.append(Ansi.EraseLine + "\n");
		builder.append(Ansi.EraseLine + "\n");
		builder.append( Ansi.EraseLine);
		builder.append(monitor.getTimer());
		builder.append(" [ ");
		builder.append(monitor.getReportLine());
		builder.append(" ] ");
		builder.append('\n' + Ansi.CursorUp);
		builder.append(Ansi.CursorUp);
		builder.append(Ansi.CursorUp);
		
		System.out.print(builder.toString());
	}
	public void startScheduledService() {
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::printReport, 1, 1, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveItemToDisk, 2, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::checkFinishState, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
		
	}
	
//	public void downloadMetaLink(List<String> urls, String saveto) {
//		try {
//
//			List<HttpUrl> httpUrls = new ArrayList<HttpUrl>();
//			File file = new File(saveto + ".rng");
//
//			for (String url : urls) {
//				httpUrls.add(HttpUrl.parse(url));
//			}
//
//			List<long[][]> ranges = new ArrayList<long[][]>();
//			long[][][] temp = Utils.fromJson(file, long[][][].class);
//			if (temp != null) {
//				for (long[][] ls : temp) {
//					ranges.add(ls);
//				}
//			}
//			temp = null;
//
//			Response response = client.head(urls.get(0));
//
//			System.out.println(response);
//
//			ResponseBody body = response.body();
//			long length = body.contentLength();
//			body.close();
//
//			if (ranges.isEmpty()) {
//				long[][] allFile = SubRange.subrange(length, httpUrls.size());
//				printRange(allFile);
//				for (int i = 0; i < allFile.length; i++) {
//					ranges.add(SubRange.subrange(allFile[i][0], allFile[i][1], 5));
//				}
//
//			}else{
//				// avoid bytes that has not been writeen befor termination 
//				ranges = avoidMissedBytes(ranges);
//				ranges = checkRanges(ranges);
//				
//			}
//
//			
//			
//			printRange(ranges);
//
//			long rem = getRengeLength(ranges);
//			System.out.print("\n\tno. #" + ranges.size());
//			System.out.print("\ttotal: " + Utils.fileLengthUnite(length));
//			System.out.print("\tdown: " + getDownLengthMB(ranges, length));
//			System.out.println("\tleft: " + Utils.fileLengthUnite(rem));
//
//			//monitor.add(file, ranges);
//			monitor.addLength(rem);
//			// monitor.start();
//
//			if (isFinish(ranges)) {
//				System.out.println("\t\tdownload complete");
//				return;
//			}
//			client.downloadMetaLinkStreamer(httpUrls, ranges, saveto, monitor);
//
//			down = true;
//		} catch (Exception e) {
//			System.out.println("Error: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}



	public void downloadFromFileAsList(String filename) {
		List<Item> items = resolver.readUrlListFromFile(filename);
		//resolver.resolveItem(items);
		items.forEach(item -> {
				if(item.getRangeInfo().isUnknowLength())
				resolver.resolveItem(item); 
				System.out.println(item);
			});
		items.forEach(item -> {
			//monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
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
//		    if(item.isFinished()) continue;
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
			if(item == null) {
				item = resolver.resolveUrl(downloadUrl);
			}else{
				item.getRangeInfo().avoidMissedBytes();
				item.getRangeInfo().checkRanges();	
			}
			
			if(item.getRangeInfo().isFinish()) {
				
				System.out.println(item);
				if (item.getRangeInfo().isFinish() ) {
					System.out.println("\t\tdownload complete");
					continue;
				}
				items.add(item);
				//monitor.add(new File(R.getConfigFile(item.getFilename() + ".json")), item);
				addItem(new File(R.getConfigFile(item.getFilename() + ".json")), item);
				long rem = item.getRangeInfo().getRengesLength();
				monitor.addLength(rem);
			}
				
		}		
		return items;
	}
	



}
