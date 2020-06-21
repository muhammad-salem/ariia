package org.ariia.web.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.stream.Collectors;

import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.mvc.resource.StreamHandler;
import org.ariia.range.RangeResponseHeader;
import org.ariia.web.app.WebServiceManager;

import com.sun.net.httpserver.HttpExchange;
import org.ariia.web.app.model.WebItem;

public class ItemService implements StreamHandler {
	
	private WebServiceManager serviceManager;
	private DataStore<Item> dataStore;
	
	public ItemService(WebServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		this.dataStore = this.serviceManager.getDataStore();
	}

	public WebItem get(Integer id) {
		return new WebItem(dataStore.findById(id));
	}

	public Integer create(Item item) {
		this.serviceManager.download(item);
		return item.getId();
	}
	
	public Integer create(String url) {
		return this.serviceManager.download(url);
	}

	public List<WebItem> getItems() {
		return dataStore.getAll().stream().map(WebItem::new).collect(Collectors.toList());
	}
	
	
	public boolean delete(Integer id) {
		return this.serviceManager.deleteAndRemoveItem(id);
	}
	
	public boolean pause(Integer id) {
		return this.serviceManager.pauseItem(id);
	}
	
	public boolean start(Integer id) {
		return this.serviceManager.startItem(id);
	}
	
	public void downloadItem(Integer id, HttpExchange exchange) {
		Log.info(getClass(), "Download Item", "item id: " + id );
		Item item =  dataStore.findById(id);
		try {
			if(item.getState().isComplete()) {
				FileInputStream stream = new FileInputStream(item.path());
				handelPlaneStream(exchange, item.getFilename(), stream);
			} else {
				// HTTP_UNAVAILABLE = 503
				exchange.sendResponseHeaders(503, -1);
				exchange.close();
			}
		} catch (Exception e) {
			Log.error(getClass(), "Download Item", "item id: " + id + "\n" + e.getMessage());
		}
	}

	public void downloadItemParts(Integer id, HttpExchange exchange, String range) {
		RangeResponseHeader header = new RangeResponseHeader(range);
		Log.info(getClass(), "setContentLength", header.start + "", header.end + "" );
		Item item =  dataStore.findById(id);
		try {
			if(item.getState().isComplete()) {
				RandomAccessFile randomAccessFile = new RandomAccessFile(item.path(), "r");
				randomAccessFile.seek(header.start);
				InputStream stream = new InputStream() {
					@Override
					public int read() throws IOException {
						if (header.end - randomAccessFile.getFilePointer() == 0) {
							return -1;
						}
						return randomAccessFile.read();
					}
					
					@Override
					public void close() throws IOException {
						randomAccessFile.close();
					}
				};
//				setContentLength(exchange.getResponseHeaders(), start, end, randomAccessFile.length());
				handelPartStream(exchange, item.getFilename(), stream, header.start, header.end, randomAccessFile.length());
			} else {
				// HTTP_UNAVAILABLE = 503
				exchange.sendResponseHeaders(503, -1);
				exchange.close();
			}
		} catch (Exception e) {
			Log.error(getClass(), "Download Item", "item id: " + id + "\n" + e.getMessage());
		}
	}

	public Integer createMetaLink(String[] urls) {
		return this.serviceManager.downloadMetaLink(urls);
	}
	

}
