package org.ariia.lunch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ariia.items.Item;
import org.ariia.items.MetalinkItem;
import org.ariia.okhttp.OkUtils;
import org.ariia.setting.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import okhttp3.Cookie;

/**
 * build only items from arguments
 * @author salem
 *
 */

public class ItemBuilder {
	
	private Argument arguments;
	private final Set<Item> items;
	
	public ItemBuilder(Argument arguments) {
		this.arguments = arguments;
		this.items = new HashSet<>();
		initItems();
	}
	
	public Set<Item> getItems() {
		return items;
	}
	
	private void initItems() {

		if (arguments.isUrl()) {
			downloadUrl();
		}
		else if (arguments.isInputFile()) {
			downloadInputFile();
		} 
		else if (arguments.isMetaLink()) {
			downloadMetalink();
		} 
		else if (arguments.isStream()) {
			streamUrl(arguments);
		}
		
//		else if (arguments.isCheckFile()) {
//			if(arguments.isDownloadPieces()) {
//				CheckManager.downloadPices(arguments.getCheckFile(), arguments.parseDownloadPieces(), arguments.parseChunkSize(), manager);
//			} else {
//				CheckManager.CheckItem(arguments.getCheckFile(),arguments.parseChunkSize(), manager);
//			}
//		}
	}

	private void addItem(String url, Map<String, String> headers) {
		Item item = new Item();
		item.setUrl(url);
		item.setHeaders(headers);
		if (arguments.isCookieFile()) {
			List<Cookie> cookies = arguments.getCookiedFileList();
			String cookie = cookies.get(0).name() + '=' + cookies.get(0).value();
			for (int i = 1; i < cookies.size(); i++) {
				cookie = cookie + "; " + cookies.get(i).name() + '=' + cookies.get(i).value();
			}
			item.addHeader("Cookie", cookie);
		}
		if (arguments.isSavePath())
			item.setSaveDir(arguments.getSavePath());
		else {
			item.setSaveDir(Properties.Default_SAVE_DIR_PATH);
		}
		if (arguments.isFileName()) {
			item.setFilename(arguments.getFileName());
		} else {
			item.setFilename(OkUtils.Filename(item.getUrl()));
		}
		items.add(item);
	}
	
	
	public void downloadUrl() {
		addItem(arguments.getUrl(), arguments.getHeaders());
	}
	
	private void streamUrl(Argument arguments) {
		addItem(arguments.getStream(), arguments.getHeaders());	
	}
	

	private void downloadInputFile() {
		List<String> lines = OkUtils.readLines(arguments.getInputFile());
		Iterator<String> iterator = lines.iterator();
		Map<String, String> headers = null;
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (line.startsWith("#")) {continue;}
			else if (line.startsWith("http")) {
				headers = new LinkedHashMap<>();
				addItem(line.trim(), headers);
			}
			else if (line.startsWith("\\t") 
					|| line.startsWith(" ")
					|| line.startsWith("	")) {
				String[] header = line.trim().split(": ");
				headers.put(header[0], header[1]);
			}
		}
	}
	

	private void downloadMetalink() {
		MetalinkItem item = null;
		String metalinkFile = arguments.getMetaLinkFile();
		if (metalinkFile.contains(".metalink")) {
			item = readMetaLink(metalinkFile);
		} else if (metalinkFile.contains(".xml")) {
			item = readMetaLinkXML(metalinkFile);
		} else {
			item = readMetaLinkText(metalinkFile);
		}
		if (Objects.nonNull(item)) {
			items.add(item);
		}
	}
	
	private MetalinkItem readMetaLink(String metaLinkFile) {

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
	
	private MetalinkItem readMetaLinkText(String metaLinkFile) {
		List<String> urls = OkUtils.readLines(metaLinkFile);
		Iterator<String> iterator = urls.iterator();
		return readMetaLinkText(iterator);
	}
	
	private MetalinkItem readMetaLinkText(Iterator<String> iterator) {
		MetalinkItem builder = new MetalinkItem();
		Map<String, String> headers = new LinkedHashMap<>();
	
		while (iterator.hasNext()) {
			String string = iterator.next();
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
	
}
