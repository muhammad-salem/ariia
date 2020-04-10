package org.ariia.core.api.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ariia.core.api.queue.StreamOrder;
import org.ariia.core.api.queue.ThreadOrder;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.response.ChannelDownloader;
import org.ariia.core.api.writer.ClientChannelWriter;
import org.ariia.core.api.writer.ClinetWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.speed.SpeedMonitor;

public class ChannelClient extends Client implements StreamOrder, ThreadOrder{
	
	ChannelDownloader channelDownloader;
	
	public ChannelClient(int retries, ClientRequest clientRequest) {
		this(retries, clientRequest, Executors.newCachedThreadPool(), new ClientChannelWriter() {});
	}
	
	public ChannelClient(int retries, ClientRequest clientRequest, ClinetWriter clinetWriter) {
		this(retries, clientRequest, Executors.newCachedThreadPool(), new ChannelDownloader(clientRequest, clinetWriter));
	}
	
	public ChannelClient(int retries, ClientRequest clientRequest, ExecutorService executor,
			ClinetWriter clinetWriter) {
		this(retries, clientRequest, executor, new ChannelDownloader(clientRequest, clinetWriter));
	}

	public ChannelClient(int retries, ClientRequest clientRequest, ExecutorService executor) {
		this(retries, clientRequest, executor, new ClientChannelWriter() {});
	}
	
	public ChannelClient(int retries, ClientRequest clientRequest, ExecutorService executor,
			ChannelDownloader channelDownloader) {
		super(retries, clientRequest, executor);
		this.channelDownloader = channelDownloader;
	}

	@Override
	public boolean downloadTask(ItemMetaData metaData, int index,
			SpeedMonitor... monitors) {
		return channelDownloader.downloadTask(metaData, index, monitors);
	}

}
