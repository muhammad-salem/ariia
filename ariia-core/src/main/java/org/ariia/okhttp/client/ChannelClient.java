package org.ariia.okhttp.client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ariia.config.OkConfig;
import org.ariia.config.Properties;
import org.ariia.manager.ItemMetaData;
import org.ariia.okhttp.queue.StreamOrder;
import org.ariia.okhttp.queue.ThreadOrder;
import org.ariia.okhttp.request.ClientRequest;
import org.ariia.okhttp.response.ChannelResponse;
import org.ariia.okhttp.writer.ClientChannelWriter;
import org.ariia.okhttp.writer.ClinetWriter;
import org.ariia.speed.SpeedMonitor;

import okhttp3.OkHttpClient;

public class ChannelClient extends Client implements ChannelResponse, StreamOrder, ThreadOrder{

	ExecutorService executor =  Executors.newCachedThreadPool();
	ExecutorService releaseResources = Executors.newCachedThreadPool();

	private ClinetWriter channelWriter;
	public ChannelClient(OkConfig config) {
		super(config);
		init(null);
	}
	public ChannelClient(OkConfig config, ClinetWriter channelWriter) {
		super(config);
		init(channelWriter);
	}

	public ChannelClient(OkHttpClient httpClient, ClinetWriter channelWriter) {
		super(httpClient);
		init(channelWriter);
	}
	public ChannelClient(OkHttpClient httpClient) {
		super(httpClient);
		init(null);
	}
	
	
	protected void init(ClinetWriter writer) {
		this.executor =  Executors.newCachedThreadPool();
		this.releaseResources = Executors.newCachedThreadPool();
		if(writer == null)
			this.channelWriter = new ClientChannelWriter() {};
	}

	@Override
	public List<Integer> downloadOrder(int rangeCount) {
		return StreamOrder.super.streamDownloadOrder(rangeCount);
	}

	@Override
	public boolean downloadTask(ItemMetaData metaData, int index,
			SpeedMonitor... monitors) {
		return ChannelResponse.super.downloadTask(metaData, index, monitors);
	}
	
	
	@Override
	public Future<?> downloadPart(ItemMetaData metaData, int index, SpeedMonitor... monitors) {
		if (Properties.RETRIES == 0) {
			return executor.submit(() -> {
				boolean finsh = false;
				while (!finsh && metaData.isDownloading()) {
					finsh = downloadTask(metaData, index, monitors);
				}
			});
		} else {
			return executor.submit(() -> {
				boolean finised = false;
				for (int i = 0; (i < Properties.RETRIES && !finised && metaData.isDownloading()); i++) {
					finised = downloadTask(metaData, index, monitors);
				}
			});
		}
	}
	
	@Override
	public ClientRequest getClientRequest() {
		return this;
	}
	@Override
	public ClinetWriter getClinetWriter() {
		return channelWriter;
	}
	@Override
	public ExecutorService getReleaseResourcesExecutor() {
		return releaseResources;
	}
	@Override
	public ExecutorService getExecutorService() {
		return executor;
	}

}
