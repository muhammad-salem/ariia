package org.ariia.web.controller;

import com.sun.net.httpserver.HttpExchange;
import org.ariia.items.Item;
import org.ariia.items.MetaLinkItem;
import org.ariia.logging.Logger;
import org.ariia.mvc.annotation.*;
import org.ariia.mvc.annotation.method.DeleteRequest;
import org.ariia.mvc.annotation.method.GetRequest;
import org.ariia.mvc.annotation.method.PostRequest;
import org.ariia.web.services.ItemService;

import java.util.Arrays;
import java.util.List;

@RestController("/items")
public class ItemController {

    private static final Logger log = Logger.create(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetRequest(path = "")
    public List<Item> items() {
        log.trace("GET ALL Items");
        return itemService.getItems();
    }

    @GetRequest(path = "/info/{id}")
    public Item getItem(@PathVariable("id") Integer id) {
        log.trace("GET Item Info", "id: " + id);
        return itemService.findById(id);
    }

    @PostRequest(path = "/create/url")
    public Integer createItem(@RequestBody String url) {
        log.trace("Create new Item", "url: " + url);
        return itemService.create(url);
    }

    @PostRequest(path = "/create/metaLink")
    public Integer createMetaLink(@RequestBody String[] urls) {
        log.trace("Create new MetaLink Item", "urls: " + Arrays.toString(urls));
        return itemService.createMetaLink(urls);
    }


    @PostRequest(path = "/create/item")
    public Integer createItem(@RequestBody Item item) {
        log.trace("Create new Item", "item: " + item);
        return itemService.create(item);
    }

    @PostRequest(path = "/{id}")
    public void updateItem(@PathVariable("id") Integer id, @RequestBody Item item) {
        log.trace("update Item", "id: " + id + "update: " + item);
        itemService.update(id, item);
    }

    @PostRequest(path = "/create/metaLinkItem")
    public Integer createMetaLink(@RequestBody MetaLinkItem item) {
        log.trace("Create new MetaLink Item", "MetaLink: " + item);
        return itemService.create(item);
    }

    @DeleteRequest(path = "/delete/{id}")
    public boolean delete(@PathVariable("id") Integer id) {
        log.trace("Delete Item Request", "id: " + id);
        return this.itemService.delete(id);
    }

    @PostRequest(path = "/pause/{id}")
    public boolean pause(@PathVariable("id") Integer id) {
        log.trace("Pause Item", "id: " + id);
        return this.itemService.pause(id);
    }

    @PostRequest(path = "/start/{id}")
    public boolean start(@PathVariable("id") Integer id) {
        log.trace("Start Download Item", "id: " + id);
        return this.itemService.start(id);
    }


    @GetRequest(path = "/download/{id}/{filename}")
    @DoExchange
    public void downloadItem(
            @PathVariable("id") Integer id,
            @PathVariable("filename") String filename,
            HttpExchange exchange) {
        log.trace("Download Request", "id: " + id + " filename: " + filename);
        this.itemService.downloadItem(id, exchange);
    }

    @GetRequest(path = "/download/{id}/{filename}", headers = {"Range"})
    @DoExchange
    public void downloadItemParts(
            @PathVariable("id") Integer id,
            @PathVariable("filename") String filename,
            @HeaderValue("Range") String range,
            HttpExchange exchange) {
        log.trace("Download Request", "id: " + id + "\nfilename: " + filename + "\nRange: " + range);
        this.itemService.downloadItemParts(id, exchange, range);
    }

}
