package org.okaria.lunch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.terminal.console.log.Log;
import org.okaria.filecheck.CheckManager;
import org.okaria.manager.Item;
import org.okaria.manager.MetalinkItem;
import org.okaria.okhttp.OkUtils;
import org.okaria.okhttp.service.ServiceManager;
import org.okaria.okhttp.writer.StreamMetaDataWriter;
import org.okaria.plugin.maven.Maven;
import org.okaria.setting.Properties;
import org.okaria.util.R;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Lunch {

	ServiceManager manager;

	public Lunch(ServiceManager manager) {
		this.manager = manager;
	}

	public void download(Argument arguments) {

		if (arguments.isUrl()) {
			downloadUrl(arguments);
		} else if (arguments.isInputFile()) {
			downloadInputFile(arguments);
		} else if (arguments.isMetaLink()) {
			downloadMetalink(arguments);
		} else if (arguments.isMaven()) {
			downloadFromMaven(arguments);
		} else if (arguments.isStream()) {
			streamUrl(arguments);
		} else if (arguments.isCheckFile()) {
			
			if(arguments.isDownloadPieces()) {
				CheckManager.downloadPices(arguments.getCheckFile(), arguments.parseDownloadPieces(), arguments.parseChunkSize(), manager);
			} else {
				CheckManager.CheckItem(arguments.getCheckFile(),arguments.parseChunkSize(), manager);
			}
		}

		// else if(arguments.isGoogleDrive()) {
		// downloadGoogleDrive(arguments.getGoogleDriveFileID());
		// }
	}

	public List<Maven> getMavens(String baseUrl, String groupId,
			String artifactId, String version, String path) {
		List<Maven> mavens = new ArrayList<>();
		if (baseUrl != null
				& (baseUrl.startsWith("/") || baseUrl.startsWith("file://"))) {

			List<String> lines = OkUtils.readLines(baseUrl);
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String string = (String) iterator.next();
				if (!string.startsWith("http")) {
					iterator.remove();
				}
			}

			for (String url : lines) {
				mavens.add(createMaven(url, null, null, null));
			}
			return mavens;
		} else {
			mavens.add(createMaven(baseUrl, groupId, artifactId, version));
		}

		return mavens;

	}

	public List<Item> mavenRepository(String baseUrl, String groupId,
			String artifactId, String version, String path) {

		if (baseUrl != null
				& (baseUrl.startsWith("/") || baseUrl.startsWith("file://"))) {

			List<String> lines = OkUtils.readLines(baseUrl);
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String string = (String) iterator.next();
				if (!string.startsWith("http")) {
					iterator.remove();
				}
			}
			List<Item> builders = new ArrayList<>();
			for (String url : lines) {
				builders.addAll(
						createItemsFromMaven(url, null, null, null, path));
			}
			return builders;
		}

		return createItemsFromMaven(baseUrl, groupId, artifactId, version,
				path);

	}

	/**
	 * @param baseUrl
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param path
	 * @return
	 */
	protected List<Item> createItemsFromMaven(String baseUrl, String groupId,
			String artifactId, String version, String path) {
		Maven mvn = new Maven(baseUrl);
		if (groupId != null)
			mvn.setGroupId(groupId);
		if (artifactId != null)
			mvn.setArtifactId(artifactId);
		if (version != null)
			mvn.setVersion(version);
		Log.trace(getClass(), "Maven", mvn.toString());
		return mvn.createBuilder(path + mvn.resolvePath());
	}

	protected Maven createMaven(String baseUrl, String groupId,
			String artifactId, String version) {
		Maven mvn = new Maven(baseUrl);
		if (groupId != null)
			mvn.setGroupId(groupId);
		if (artifactId != null)
			mvn.setArtifactId(artifactId);
		if (version != null)
			mvn.setVersion(version);
		Log.trace(getClass(), "Maven", mvn.toString());
		return mvn;
	}

	public List<Item> readListFile(String inputfile) {
		List<String> urls = OkUtils.readLines(inputfile);

		Iterator<String> iterator = urls.iterator();
		List<Item> builders = new LinkedList<>();
		Item builder = null;
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
				builder = new Item();
				headers = new LinkedHashMap<>();
				builder.setUrl(string);
			} else if (string.startsWith("\\t") 
					|| string.startsWith(" ")
					|| string.startsWith("	")) {
				if (headers != null) {
					int index = string.indexOf(": ");
					headers.put(string.substring(1, index),
							string.substring(index + 2));
				}

			} else {
				if(headers != null) {
					builder.setHeaders(headers);
				}
			}
		}
		if (builder != null)
			builders.add(builder);

		return builders;
	}

	public MetalinkItem readMetaLink(String metaLinkFile) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(metaLinkFile));
			NodeList mirrors = document.getElementsByTagName("url");
			List<String> urls = new ArrayList<>();

			for (int i = 0; i < mirrors.getLength(); i++) {
				Node node = mirrors.item(i);
				if (node.hasAttributes() && node.getAttributes()
						.getNamedItem("type").getNodeValue().equals("http"))
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
	public MetalinkItem readMetaLinkXML(String metaLinkFile) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(metaLinkFile));
			NodeList mirrors = document.getElementsByTagName("mirror");
			List<String> urls = new ArrayList<>();

			for (int i = 0; i < mirrors.getLength(); i++) {
				Node node = mirrors.item(i);
				urls.add(node.getAttributes().getNamedItem("url")
						.getNodeValue());
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
	public MetalinkItem readMetaLinkText(String metaLinkFile) {
		List<String> urls = OkUtils.readLines(metaLinkFile);
		Iterator<String> iterator = urls.iterator();
		return readMetaLinkText(iterator);
	}

	private MetalinkItem readMetaLinkText(Iterator<String> iterator) {
		MetalinkItem builder = new MetalinkItem();
		Map<String, String> headers = new LinkedHashMap<>();

		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			if (string.startsWith("#")) {
				iterator.remove();
			} else if (string.startsWith("http")) {
				builder.addMirror(string);
			} else if (string.startsWith("\t")) {
				int index = string.indexOf(": ");
				headers.put(string.substring(1, index),
						string.substring(index + 2));
			}
		}
		builder.setHeaders(headers);
		return builder;
	}

	// private void downloadGoogleDrive(String fileID) {
	// GoogleDriveFile drive = new GoogleDriveFile(fileID);
	// try {
	// HttpUrl url = HttpUrl.parse(drive.setupRequestUrl());
	// Response response = client.get(url);
	// drive.confirm(response.headers());
	//
	// List<Cookie> cookies =
	// client.getHttpClient().cookieJar().loadForRequest(url);
	// drive.setCookies(cookies);
	// for (Cookie cookie : cookies) {
	// Log.fine(getClass(), "cookie",
	// cookie.name() + " " +
	// cookie.domain() + " " +
	// cookie.value() + " " +
	// cookie.expiresAt() + " " +
	// cookie.path()
	// );
	//
	// }
	// response.close();
	//
	// url = HttpUrl.parse(drive.url());
	//
	// response = client.get(url, cookies);
	//
	//
	// response.close();
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	//
	// }

	
	private void streamUrl(Argument arguments) {
		Item item = new Item();
		item.setUrl(arguments.getStream());
		configBuilder(arguments, item);
		if (arguments.isFileName())
			item.setFilename(arguments.getFileName());
		buildItem(item);
		setCache(item);
		if (!item.getFilename().equals("404_Not_Found")) {
			// addItem2WattingList(item);

			manager.getWattingList().add(new StreamMetaDataWriter(item));
			manager.getSessionMointor().add(item.getRangeInfo());
		}
		//
	}

	private void downloadFromMaven(Argument arg) {
		String saveto = arg.getMavenRepository();
		if (saveto == null)
			saveto = arg.getSavePath();
		if (saveto == null)
			saveto = Maven.MAVEN_REPOSITORY;

		List<Maven> mavens = getMavens(arg.getMaven(), arg.getMavenGroupId(),
				arg.getMavenArtifactId(), arg.getMavenVersion(), saveto);

		for (Maven maven : mavens) {
			List<Item> items = maven.getItems(saveto + maven.resolvePath(), maven.mainFiles());
			for (Item item : items) {
				buildItem(item);
				item.setCacheFile(null);
				if (!item.getFilename().equals("404_Not_Found")) {
					addItem2WattingList(item);
					List<Item> cItems = maven.getItems(saveto + maven.resolvePath(), maven.certificatedFiles(item.getUrl()));
					for (Item item2 : cItems) {
						buildItem(item2);
						item2.setCacheFile(null);
						if (!item2.getFilename().equals("404_Not_Found")) {
							addItem2WattingList(item2);
						}
					}
				}
			}

		}

		// List<Item> builders = mavenRepository(
		// arg.getMaven(),
		// arg.getMavenGroupId(),
		// arg.getMavenArtifactId(),
		// arg.getMavenVersion(),
		// saveto);
		//
		//
		// Iterator<Item> iterator = builders.iterator();
		// while (iterator.hasNext()) {
		// Item item = (Item) iterator.next();
		// item = buildItem(item);
		// if(item.getFilename().equals("404_Not_Found")) {
		// String name = item.getFilename();
		// iterator.remove();
		// while (iterator.hasNext()) {
		// item = (Item) iterator.next();
		// if(item.getUrl().contains(name))
		// iterator.remove();
		// else break;
		// }
		// }else {
		// addItem2WattingList(item);
		// }
		//
		// }

		// for (Item builder : builders) {
		// Item item = buildItem(builder);
		// item.setCacheFile(null);
		// if(!item.getFilename().equals("404_Not_Found")) {
		// addItem2WattingList(item);
		// }else {
		//
		// }
		//
		// }

	}

	public void downloadUrl(Argument arguments) {
		Item item = new Item();
		item.setUrl(arguments.getUrl());
		configBuilder(arguments, item);
		if (arguments.isFileName())
			item.setFilename(arguments.getFileName());
		buildItem(item);
		if (!item.getFilename().equals("404_Not_Found"))
			addItem2WattingList(item);
	}

	public void downloadInputFile(Argument arguments) {
		List<Item> items = readListFile(arguments.getInputFile());
		for (Item item : items) {
			configBuilder(arguments, item);
			buildItem(item);
			if (!item.getFilename().equals("404_Not_Found"))
				addItem2WattingList(item);
		}
	}

	public void downloadMetalink(Argument arguments) {
		MetalinkItem item = null;
		String metalinkFile = arguments.getMetaLinkFile();
		if (metalinkFile.contains(".metalink")) {
			item = readMetaLink(metalinkFile);
		} else if (metalinkFile.contains(".xml")) {
			item = readMetaLinkXML(metalinkFile);
		} else {
			item = readMetaLinkText(metalinkFile);
		}
		if (item == null)
			return;
		configBuilder(arguments, item);
		buildItem(item);
		if (!item.getFilename().equals("404_Not_Found"))
			addItem2WattingList(item);
	}

	/**
	 * @param item
	 */
	protected void setCache(Item item) {
		item.setCacheFile(R.getConfigPath(OkUtils.Filename(item.getUrl())) + ".json");
	}

	/**
	 * @param arguments
	 * @param builder
	 */
	protected void configBuilder(Argument arguments, Item builder) {

		builder.addHeaders(arguments.getHeaders());
		builder.addCookies(arguments.getAllCookie());
		if (arguments.isReferer())
			builder.setReferer(arguments.getReferer());
		if (arguments.isUserAgent())
			builder.setUseragent(arguments.getUserAgent());
		if (arguments.isSavePath())
			builder.setFolder(arguments.getSavePath());
		else {
			if (builder.getFolder() == null)
				builder.setFolder(Properties.Default_SAVE_DIR_PATH);
		}

	}

	protected void buildItem(final Item item) {
		Item temp = manager.getItemStore().searchByUrl(item.getUrl());
		if (temp == null) {
			manager.getClient().updateItemOnline(item);
			setCache(item);
		} else {
			//temp.getRangeInfo().avoidMissedBytes();
			temp.getRangeInfo().checkRanges();
			temp.addHeaders(item.getHeaders());
			item.copy(temp);
		}
	}

	public void addItem2WattingList(Item item) {
		if (item.isStreaming()) {
			Log.info(getClass(), "add stream URL to waiting download list ",
					item.liteString());
		} else if (item.isFinish()) {
			Log.info(getClass(), "Download Complete for URL", item.liteString());
			return;
		} else {
			Log.info(getClass(), "add URL to waiting download list",
					item.toString());
		}

		manager.addItemToWattingList(item);
		Item.toJsonFile(item);
	}

}
