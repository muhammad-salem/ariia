package org.ariia.web.services;

import com.sun.net.httpserver.HttpExchange;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.Builder;
import org.ariia.items.Item;
import org.ariia.items.MetaLinkItem;
import org.ariia.logging.Log;
import org.ariia.logging.Logger;
import org.ariia.mvc.resource.StreamHandler;
import org.ariia.range.RangeResponseHeader;
import org.ariia.web.app.WebDownloadService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

public class ItemService implements StreamHandler {

    Logger log = Logger.create(ItemService.class);

    private WebDownloadService downloadService;

    public ItemService(WebDownloadService downloadService) {
        this.downloadService = Objects.requireNonNull(downloadService);
    }

    public Item findById(Integer id) {
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            return optional.get().getItem();
        } else {
            throw new NullPointerException("no download found with id: " + id);
        }
    }

    public Integer create(Item item) {
        this.downloadService.initializeItemOnlineAndDownload(item);
        return item.getId();
    }

    public void update(Integer id,  Item update) {
        var optionalItemMetaData = downloadService.searchById(id);
        if (!optionalItemMetaData.isPresent()){
            return;
        }
        var metaData = optionalItemMetaData.get();
        boolean isDownloading = metaData.isDownloading();
        if (isDownloading){
            this.pause(id);
        }
        metaData.getItem().update(update);
        if (isDownloading){
            this.start(id);
        }
    }

    public Integer create(String url) {
        return this.create(url, Collections.emptyMap());
    }

    public Integer create(String url, Map<String, List<String>> headers) {
        var builder = new Builder(url);
        builder.saveDir(downloadService.getProperties().getDefaultSaveDirectory());
        builder.addHeaders(headers);
        var item = builder.build();
        this.downloadService.initializeItemOnlineAndDownload(item);
        return item.getId();
    }

    public List<Item> getItems() {
        return downloadService.itemStream().map(ItemMetaData::getItem).collect(Collectors.toList());
    }


    public boolean delete(Integer id) {
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            downloadService.deleteFromList(optional.get());
            return true;
        }
        return false;
    }

    public boolean pause(Integer id) {
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            downloadService.moveToPauseList(optional.get());
            return true;
        }
        return false;
    }

    public boolean start(Integer id) {
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            downloadService.moveToDownloadList(optional.get());
            return true;
        }
        return false;
    }

    public void downloadItem(Integer id, HttpExchange exchange) {
        log.info("Download Item", "item id: " + id);
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            var item = optional.get().getItem();
            try {
                if (item.getState().isComplete()) {
                    var stream = new FileInputStream(item.path());
                    handelPlaneStream(exchange, item.getFilename(), stream);
                } else {
                    // HTTP_UNAVAILABLE = 503
                    exchange.sendResponseHeaders(503, -1);
                    exchange.close();
                }
            } catch (Exception e) {
                log.error("Download Item", "item id: " + id + "\n" + e.getMessage());
            }
        } else {
            throw new NullPointerException("no Item found with id: " + id);
        }
    }

    public void downloadItemParts(Integer id, HttpExchange exchange, String range) {
        var header = new RangeResponseHeader(range);
        log.info("setContentLength", header.start + "", header.end + "");
        var optional = downloadService.searchById(id);
        if (optional.isPresent()) {
            var item = optional.get().getItem();
            try {
                if (item.getState().isComplete()) {
                    var randomAccessFile = new RandomAccessFile(item.path(), "r");
                    randomAccessFile.seek(header.start);
                    var stream = new InputStream() {
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
                log.error("Download Item", "item id: " + id + "\n" + e.getMessage());
            }
        } else {
            throw new NullPointerException("no Item found with id: " + id);
        }
    }

    public Integer createMetaLink(String[] urls) {
        return createMetaLink(urls, Collections.emptyMap());
    }

    public Integer createMetaLink(String[] urls, Map<String, List<String>> headers) {
        var metalinkItem = new MetaLinkItem();
        metalinkItem.setSaveDirectory(downloadService.getProperties().getDefaultSaveDirectory());
        metalinkItem.addHeaders(headers);
        for (String string : urls) {
            metalinkItem.addMirror(string);
        }
        this.downloadService.initializeItemOnlineAndDownload(metalinkItem);
        return metalinkItem.getId();
    }


}
