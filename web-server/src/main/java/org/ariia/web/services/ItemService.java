package org.ariia.web.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Builder;
import org.ariia.items.Item;
import org.ariia.items.MetaLinkItem;
import org.ariia.logging.Log;
import org.ariia.mvc.resource.StreamHandler;
import org.ariia.range.RangeResponseHeader;
import org.ariia.web.app.WebDownloadService;

import com.sun.net.httpserver.HttpExchange;

public class ItemService implements StreamHandler {

	private WebDownloadService downloadService;
	
	public ItemService(WebDownloadService downloadService) {
		this.downloadService = Objects.requireNonNull(downloadService);
	}

	public Item findById(Integer id) {
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()){
			return optional.get().getItem();
		} else {
			throw new NullPointerException("no download found with id: " + id);
		}
	}

	public Integer create(Item item) {
		this.downloadService.initializeItemOnlineAndDownload(item);
		return item.getId();
	}

	public Integer create(String url) {
        return this.create(url, Collections.emptyMap());
    }

    public Integer create(String url, Map<String, List<String>> headers) {
        Builder builder = new Builder(url);
        builder.saveDir(downloadService.getProperties().getDefaultSaveDirectory());
        builder.addHeaders(headers);
        Item item = builder.build();
        this.downloadService.initializeItemOnlineAndDownload(item);
        return item.getId();
    }

	public List<Item> getItems() {
		return downloadService.itemStream().map(ItemMetaData::getItem).collect(Collectors.toList());
	}
	
	
	public boolean delete(Integer id) {
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()) {
			downloadService.deleteFromList(optional.get());
			return true;
		}
		return false;
	}
	
	public boolean pause(Integer id) {
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()) {
			downloadService.moveToPauseList(optional.get());
			return true;
		}
		return false;
	}
	
	public boolean start(Integer id) {
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()) {
			downloadService.moveToDownloadList(optional.get());
			return true;
		}
		return false;
	}
	
	public void downloadItem(Integer id, HttpExchange exchange) {
		Log.info(getClass(), "Download Item", "item id: " + id );
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()) {
			Item item =  optional.get().getItem();
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
		} else {
			throw new NullPointerException("no Item found with id: " + id);
		}
	}

	public void downloadItemParts(Integer id, HttpExchange exchange, String range) {
		RangeResponseHeader header = new RangeResponseHeader(range);
		Log.info(getClass(), "setContentLength", header.start + "", header.end + "" );
		Optional<ItemMetaData> optional = downloadService.searchById(id);
		if (optional.isPresent()) {
			Item item =  optional.get().getItem();
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
					handelPartStream(exchange, item.getFilename(), stream, header.start, header.end, randomAccessFile.length());
				} else {
					// HTTP_UNAVAILABLE = 503
					exchange.sendResponseHeaders(503, -1);
					exchange.close();
				}
			} catch (Exception e) {
				Log.error(getClass(), "Download Item", "item id: " + id + "\n" + e.getMessage());
			}
		} else {
			throw new NullPointerException("no Item found with id: " + id);
		}
	}

	public Integer createMetaLink(String[] urls) {
        return createMetaLink(urls, Collections.emptyMap());
    }

    public Integer createMetaLink(String[] urls, Map<String, List<String>> headers) {
        MetaLinkItem metalinkItem = new MetaLinkItem();
        metalinkItem.setSaveDirectory(downloadService.getProperties().getDefaultSaveDirectory());
        metalinkItem.addHeaders(headers);
        for (String string : urls) {
            metalinkItem.addMirror(string);
        }
        this.downloadService.initializeItemOnlineAndDownload(metalinkItem);
        return metalinkItem.getId();
    }


}
