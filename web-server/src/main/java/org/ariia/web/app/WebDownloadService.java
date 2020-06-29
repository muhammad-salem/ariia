package org.ariia.web.app;

import org.ariia.core.api.service.DownloadService;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.logging.Log;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.WebItem;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebDownloadService extends DownloadService {

    protected SourceEvent sourceEvent;
    protected EventProvider sessionProvider;
    protected EventProvider itemListProvider;
    protected EventProvider itemProvider;

    public WebDownloadService(SourceEvent sourceEvent) {
        super();
        this.sourceEvent = Objects.requireNonNull(sourceEvent);
        this.sessionProvider = new EventProvider("session-monitor", sourceEvent);
        this.itemListProvider = new EventProvider("item-list", sourceEvent);
        this.itemProvider = new EventProvider("item", sourceEvent);
    }


    @Override
    public void startScheduledService() {
        itemDataStore.getAll().forEach(this::download);
        super.startScheduledService();
        sourceEvent.send("session-start");
    }

    @Override
    public void runSystemShutdownHook() {
        super.runSystemShutdownHook();
        sourceEvent.send("session-shutdown");
    }

    @Override
    public void printReport() {
        super.printReport();
        this.sendWebReport();
    }

    private String toJsonItem(ItemMetaData metaData) {
        return Utils.toJson(new WebItem(metaData));
    }

    private String toJsonItemsList(Stream<ItemMetaData> itemStream) {
        List<WebItem> dataList = itemStream.map(WebItem::new).collect(Collectors.toList());
        if (dataList.isEmpty()) return null;
        return Utils.toJson(dataList);
    }

    private void sendWebReport() {
        try {
            sessionProvider.send(Utils.toJson(sessionReport));
            String downloadMessage = toJsonItemsList(downloadStream());
            if (Objects.nonNull(downloadMessage)) {
                itemListProvider.send(downloadMessage);
            }
        } catch (Exception e) {
            Log.error(getClass(), "Send web Report Error", e.getMessage());
        }
    }

    @Override
    protected void waitEvent(ItemMetaData item) {
        super.waitEvent(item);
        itemProvider.send(toJsonItem(item));
    }

    @Override
    protected void downloadEvent(ItemMetaData item) {
        super.downloadEvent(item);
        itemProvider.send(toJsonItem(item));
    }

    @Override
    protected void pauseEvent(ItemMetaData item) {
        super.pauseEvent(item);
        itemProvider.send(toJsonItem(item));
    }

    @Override
    protected void completeEvent(ItemMetaData item) {
        super.completeEvent(item);
        itemProvider.send(toJsonItem(item));
    }

    public Optional<ItemMetaData> searchById(Integer id) {
        return itemMetaDataList.stream()
                .filter(item -> Objects.equals(item.getItem().getId(), id))
                .findAny();
    }
}
