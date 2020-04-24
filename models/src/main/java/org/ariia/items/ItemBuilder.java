package org.ariia.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import org.ariia.args.Argument;
import org.ariia.config.Properties;
import org.ariia.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * build only items from arguments
 * @author salem
 *
 */

public class ItemBuilder {
	
	private Argument arguments;
	private Set<Item> items;
	
	public ItemBuilder(Argument arguments) {
		setArguments(arguments);
	}
	
	public Set<Item> getItems() {
		return items;
	}

	public void clear() {
		this.items.clear();
		this.items = null;
	}
	
	public void setArguments(Argument arguments) {
		this.arguments = arguments;
		initItems();
	}
	
	
	private void initItems() {
		this.items = new HashSet<>();
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

	private void addItem(String url, Map<String, List<String>> headers) {
		Item item = builditemOf(url, headers);
		items.add(item);
	}

	public Item builditemOf(String url) {
		return builditemOf(url, Collections.emptyMap());
	}
	
	public Item builditemOf(String url, Map<String, List<String>> headers) {
		Item item = new Item();
		item.setUrl(url);
		item.setHeaders(headers);
		if (arguments.isCookieFile()) {
			Map<String, String> cookies = arguments.getCookies();
			List<String> values = new ArrayList<>(cookies.size());
//			String cookie = "";
			for (Map.Entry<String, String> entry : cookies.entrySet()) {
				values.add(entry.getKey() + '=' + entry.getValue());
//				cookie = entry.getKey() + '=' + entry.getValue() + "; " + cookie;
			}
			item.addHeader("Cookie", values);
		}
		if (arguments.isSavePath())
			item.setSaveDirectory(arguments.getSavePath());
		else {
			item.setSaveDirectory(Properties.Default_SAVE_DIR_PATH);
		}
		if (arguments.isFileName()) {
			item.setFilename(arguments.getFileName());
		} else {
			File file = new File(url);
			String fileName = file.getName().split("\\?")[0];
			
			if ("".equals(fileName)) {
				String[] fileParts = url.split("/");
				fileName = fileParts[fileParts.length-2].split("\\?")[0];
			}
			item.setFilename(fileName);
		}
		return item;
	}
	
	
	public void downloadUrl() {
		addItem(arguments.getUrl(), arguments.getHeaders());
	}
	
	private void streamUrl(Argument arguments) {
		addItem(arguments.getStream(), arguments.getHeaders());	
	}
	

	private void downloadInputFile() {
		List<String> lines = Utils.readLines(arguments.getInputFile());
		Iterator<String> iterator = lines.iterator();
		Map<String, List<String>> headers = null;
		while (iterator.hasNext()) {
			String line = iterator.next();
			if (line.startsWith("#")) {continue;}
			else if (line.startsWith("http")) {
				headers = new LinkedHashMap<>();
				addItem(line.trim(), headers);
			}
			else if (line.equals("\t")) {
				continue;
			}
			else if (line.startsWith("\\t") 
					|| line.startsWith(" ")
					|| line.startsWith("	")) {
				String[] header = line.trim().split(": ");
				if (header.length == 1) {
					continue;
				}
				List<String> value = headers.getOrDefault(header[0], new ArrayList<>(1));
				value.add(header[1]);
				headers.put(header[0], value);
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
		List<String> urls = Utils.readLines(metaLinkFile);
		Iterator<String> iterator = urls.iterator();
		return readMetaLinkText(iterator);
	}
	
	private MetalinkItem readMetaLinkText(Iterator<String> iterator) {
		MetalinkItem builder = new MetalinkItem();
		Map<String, List<String>> headers = new LinkedHashMap<>();
	
		while (iterator.hasNext()) {
			String string = iterator.next();
			if (string.startsWith("#")) {
				iterator.remove();
			} else if (string.startsWith("http")) {
				builder.addMirror(string);
			} else if (string.startsWith("\t")) {
				int index = string.indexOf(": ");
				String headerName = string.substring(1, index);
				String headerValue = string.substring(index + 2);
				List<String> value = headers.getOrDefault(headerName, new ArrayList<>(1));
				value.add(headerValue);
				headers.put(headerName, value);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
}
