package org.ariia.web.app;

import lombok.Getter;
import org.ariia.core.api.service.DownloadService;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.logging.Log;
import org.ariia.mvc.sse.EventProvider;
import org.ariia.mvc.sse.SourceEvent;
import org.ariia.util.Utils;
import org.ariia.web.app.model.WebItem;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class BroadcastEventService {
    private final DownloadService downloadService;
    private final SourceEvent sourceEvent;
    private final EventProvider sessionProvider;
    private final EventProvider itemListProvider;
    private final EventProvider itemProvider;

    private final Flow.Subscriber<ItemMetaData> onUpdateSubscriber = new Flow.Subscriber<>() {
        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(ItemMetaData item) {
            itemProvider.send(toJsonItem(item));
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {}

        @Override
        public void onComplete() {}

    };

    private final Flow.Subscriber<Void> onCycleUpdateSubscriber = new Flow.Subscriber<>() {
        private Flow.Subscription subscription;
        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(Void unused) {
            BroadcastEventService.this.sendWebReport();
            this.subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {}

        @Override
        public void onComplete() {}

    };

    public BroadcastEventService(DownloadService downloadService, SourceEvent sourceEvent) {
        this.downloadService = Objects.requireNonNull(downloadService);
        this.sourceEvent = Objects.requireNonNull(sourceEvent);
        this.sessionProvider = new EventProvider("session-monitor", sourceEvent);
        this.itemListProvider = new EventProvider("item-list", sourceEvent);
        this.itemProvider = new EventProvider("item", sourceEvent);
    }


    public void sendEndSession() {
        sourceEvent.send("session-start");
    }

    public void sendStartSession() {
        sourceEvent.send("session-shutdown");
    }

    private String toJsonItem(ItemMetaData metaData) {
        return Utils.toJson(new WebItem(metaData));
    }

    private String toJsonItemsList(Stream<ItemMetaData> itemStream) {
        var dataList = itemStream.map(WebItem::new).collect(Collectors.toList());
        if (dataList.isEmpty()) return null;
        return Utils.toJson(dataList);
    }

    private void sendWebReport() {
        try {
            sessionProvider.send(Utils.toJson(downloadService.getSessionReport()));
            var downloadMessage = toJsonItemsList(downloadService.downloadStream());
            if (Objects.nonNull(downloadMessage)) {
                itemListProvider.send(downloadMessage);
            }
        } catch (Exception e) {
            Log.error(getClass(), "Send web Report Error", e.getMessage());
        }
    }

    public void initEvent() {
        this.downloadService.getUpdatePublisher().subscribe(this.onUpdateSubscriber);
        this.downloadService.getCyclePublisher().subscribe(this.onCycleUpdateSubscriber);
    }
}
