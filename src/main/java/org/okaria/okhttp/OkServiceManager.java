package org.okaria.okhttp;

import java.io.File;
import java.io.IOException;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.lunch.Argument;
import org.okaria.lunch.FilenameStore;
import org.okaria.manager.GoogleDriveFile;
import org.okaria.manager.Item;
import org.okaria.manager.ItemIndictor;
import org.okaria.manager.Maven;
import org.okaria.manager.MetalinkItem;
import org.okaria.range.RangeInfo;
import org.okaria.range.RangeInfoMonitor;
import org.okaria.range.RangeUtils;
import org.okaria.setting.AriaProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class OkServiceManager implements RangeUtils {

	
	public final static int SCHEDULE_TIME = 1;
	public final static int SCHEDULE_POOL = 5;

	ScheduledExecutorService cheduledService;

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
		monitor = new RangeInfoMonitor(); 
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
			System.out.println("\u001B[7B\u001B[0m");
		}));

	}


	public void startScheduledService() {
		
		cheduledService.execute(this::saveWattingItemToDisk);
		
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveDownloadingItemToDisk, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
		
	}

	
	public void startScheduledService(boolean printSpeedReport) {
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::checkdownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		if(printSpeedReport)
			cheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
		
		cheduledService.scheduleWithFixedDelay(this::saveDownloadingItemToDisk, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
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
		if(downloadingList.size() < AriaProperties.MAX_ACTIVE_DOWNLOAD_POOL) {
			while (downloadingList.size() < AriaProperties.MAX_ACTIVE_DOWNLOAD_POOL && !wattingList.isEmpty()) {
				ItemIndictor indictor = wattingList.poll();
				downloadingList.add(indictor);
				Log.info(getClass(), "Add Item to download list", indictor.getItem().getFilename());
			}
		}
		
		
		List<ItemIndictor> removeList = new ArrayList<>();
		for (ItemIndictor indictor : downloadingList) {
			Item item = indictor.getItem();
			RangeInfo info = item.getRangeInfo();
			if (info.isFinish()) {
				Log.info(getClass(), "Remove Item from download list", item.getFilename());
				removeList.add(indictor);
				continue;
			}else if(! indictor.isDownloading()) {
				indictor.download(client, monitor/*, indictor.getItemMointor()*/);
			}
			indictor.checkDoneFuturesFromMax(client, monitor/*, indictor.getItemMointor()*/);
			
		}
		
		removeList.forEach((indictor)->{
			Log.info(getClass(), "Item Download Complete", indictor.getItem().liteString());
			downloadingList.remove(indictor);
			indictor.saveItem2CacheFile();
		});
		
		
		
		//downloadingList.removeAll(removeList);
		
		if (downloadingList.isEmpty() & wattingList.isEmpty()) {
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
	
	public List<Item.Builder> mavenRepository(String baseUrl, String groupId, String artifactId, String version, String path){
		Maven mvn = new Maven(baseUrl);
		mvn.setGroupId(groupId);
		mvn.setArtifactId(artifactId);;
		mvn.setVersion(version);		
		Log.fine(getClass(), "Maven", mvn.toString());
		return mvn.generateBuilder(path + mvn.resolvePath());
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

	public MetalinkItem.Builder readMetaLink(String metaLinkFile) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder =  factory.newDocumentBuilder();
			Document document = builder.parse(new File(metaLinkFile));
			NodeList mirrors =  document.getElementsByTagName("url");
			List<String> urls = new ArrayList<>();
			
			for (int i = 0; i < mirrors.getLength(); i++) {
				Node node = mirrors.item(i);
				if(node.hasAttributes() && node.getAttributes().getNamedItem("type").getNodeValue().equals("http"))
					urls.add(node.getTextContent());
			}
			Iterator<String> iterator = urls.iterator();
			return readMetaLinkText(iterator);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public MetalinkItem.Builder readMetaLinkXML(String metaLinkFile) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder =  factory.newDocumentBuilder();
			Document document = builder.parse(new File(metaLinkFile));
			NodeList mirrors =  document.getElementsByTagName("mirror");
			List<String> urls = new ArrayList<>();
			
			for (int i = 0; i < mirrors.getLength(); i++) {
				Node node = mirrors.item(i);
				urls.add(node.getAttributes().getNamedItem("url").getNodeValue());
			}
			Iterator<String> iterator = urls.iterator();
			return readMetaLinkText(iterator);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	public MetalinkItem.Builder readMetaLinkText(String metaLinkFile) {
		List<String> urls = OkUtils.readLines(metaLinkFile);
		Iterator<String> iterator = urls.iterator();
		return readMetaLinkText(iterator);
	}
	
	private MetalinkItem.Builder readMetaLinkText(Iterator<String> iterator ) {
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
		else if(arguments.isMaven()) {
			downloadFromMaven(arguments);
		}
		else if(arguments.isGoogleDrive()) {
			downloadGoogleDrive(arguments.getGoogleDriveFileID());
		}
	}
	

	private void downloadGoogleDrive(String fileID) {
		GoogleDriveFile drive = new GoogleDriveFile(fileID);
		try {
			HttpUrl url = HttpUrl.parse(drive.setupRequestUrl());
			Response response = client.get(url);
			drive.confirm(response.headers());
			
			List<Cookie> cookies = client.getHttpClient().cookieJar().loadForRequest(url);
			drive.setCookies(cookies);
			for (Cookie cookie : cookies) {
				Log.fine(getClass(), "cookie", 
						cookie.name() + " " + 
						cookie.domain() + " " + 
						cookie.value() + " " + 
						cookie.expiresAt() + " " + 
						cookie.path()
						);
				
			}
			response.close();
			
			url = HttpUrl.parse(drive.url());
			
			response = client.get(url, cookies);
			
			
			response.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}


	private void downloadFromMaven(Argument arg) {
		String saveto = arg.getMavenRepository();
		if(saveto == null) saveto = arg.getSavePath();
		if(saveto == null) saveto = Maven.MAVEN_REPOSITORY;
		List<Item.Builder> builders = mavenRepository(
				arg.getMaven(),
				arg.getMavenGroupId(), 
				arg.getMavenArtifactId(), 
				arg.getMavenVersion(), 
				saveto);
		for (Item.Builder builder : builders) {
			configBuilder(arg, builder);
			Item item = buildItem(builder);
			addItem2WattingList(item);
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
	}
	

	public void downloadInputFile(Argument arguments) {
		List<Item.Builder> builders = readListFile(arguments.getInputFile());
		for (Item.Builder builder : builders) {
			configBuilder(arguments, builder);
			Item item = buildItem(builder);
			addItem2WattingList(item);
		}
	}
	
	public void downloadMetalink(Argument arguments) {
		MetalinkItem.Builder builder = null;
		String metalinkFile = arguments.getMetaLinkFile();
		if( metalinkFile.contains(".metalink")) {
			builder = readMetaLink(metalinkFile);
		}else if(metalinkFile.contains(".xml")) {
			builder = readMetaLinkXML(metalinkFile);
		}else {
			builder = readMetaLinkText(metalinkFile);
		}
		if(builder == null) return;
		configBuilder(arguments, builder);
		MetalinkItem item = buildItem(builder);
		addItem2WattingList(item);
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
		if( item.isStreaming() ) {
			Log.info(getClass(), "add stream item to watting list ", item.liteString());
		}
		else if( item.isFinish() ) {
			Log.info(getClass(), "Complete Download", item.liteString());
			return false;
		}else {
			Log.info(getClass(), "add download item to watting list", item.toString());
		}
		
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
