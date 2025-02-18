package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.queue.StreamOrder;
import org.ariia.core.api.queue.ThreadOrder;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.response.SegmentDownloader;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.SegmentWriter;
import org.network.speed.report.SpeedMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SegmentClient extends Client implements StreamOrder, ThreadOrder {

    private final SegmentDownloader segmentDownloader;

    public SegmentClient(Properties properties, ClientRequest clientRequest) {
        this(
                properties,
                clientRequest,
                Executors.newVirtualThreadPerTaskExecutor(),
                new SegmentDownloader(clientRequest, new SegmentWriter() {}))
        ;
    }

    public SegmentClient(Properties properties, ClientRequest clientRequest, ExecutorService executor) {
        this(
                properties,
                clientRequest,
                executor,
                new SegmentDownloader(clientRequest, new SegmentWriter() {})
        );
    }


    public SegmentClient(Properties properties, ClientRequest clientRequest, ExecutorService executor,
                         SegmentWriter segmentWriter) {
        this(properties, clientRequest, executor, new SegmentDownloader(clientRequest, segmentWriter));
    }

    public SegmentClient(Properties properties, ClientRequest clientRequest, ExecutorService executor,
                         SegmentDownloader segmentDownloader) {
        super(properties, clientRequest, executor);
        this.segmentDownloader = segmentDownloader;
    }

    @Override
    public boolean downloadTask(ItemMetaData metaData, int index,
                                SpeedMonitor... monitors) {
        return segmentDownloader.downloadTask(metaData, index, monitors);
    }

    @Override
    public int getRangePoolNum() {
        return properties.getRangePoolNum();
    }

}
