package org.ariia.core.api.client;

import org.ariia.config.Properties;
import org.ariia.core.api.queue.StreamOrder;
import org.ariia.core.api.queue.ThreadOrder;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.response.ChannelDownloader;
import org.ariia.core.api.writer.ClientChannelWriter;
import org.ariia.core.api.writer.ClientWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.network.speed.report.SpeedMonitor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChannelClient extends Client implements StreamOrder, ThreadOrder {

    private final ChannelDownloader channelDownloader;

    public ChannelClient(Properties properties, ClientRequest clientRequest) {
        this(
                properties,
                clientRequest,
                Executors.newVirtualThreadPerTaskExecutor(),
                new ClientChannelWriter() {}
        );
    }

    public ChannelClient(Properties properties, ClientRequest clientRequest, ClientWriter clientWriter) {
        this(properties, clientRequest, Executors.newVirtualThreadPerTaskExecutor(), new ChannelDownloader(clientRequest, clientWriter));
    }

    public ChannelClient(Properties properties, ClientRequest clientRequest, ExecutorService executor,
                         ClientWriter clientWriter) {
        this(properties, clientRequest, executor, new ChannelDownloader(clientRequest, clientWriter));
    }

    public ChannelClient(Properties properties, ClientRequest clientRequest, ExecutorService executor) {
        this(properties, clientRequest, executor, new ClientChannelWriter() {});
    }

    public ChannelClient(Properties properties, ClientRequest clientRequest, ExecutorService executor,
                         ChannelDownloader channelDownloader) {
        super(properties, clientRequest, executor);
        this.channelDownloader = channelDownloader;
    }

    @Override
    public boolean downloadTask(ItemMetaData metaData, int index,
                                SpeedMonitor... monitors) {
        return channelDownloader.downloadTask(metaData, index, monitors);
    }

    @Override
    public int getRangePoolNum() {
        return properties.getRangePoolNum();
    }

}
