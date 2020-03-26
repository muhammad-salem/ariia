package org.okaria.okhttp.client;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.okaria.core.OkConfig;
import org.okaria.manager.ItemMetaData;
import org.okaria.okhttp.queue.StreamOrder;
import org.okaria.okhttp.queue.ThreadOrder;
import org.okaria.okhttp.request.ClientRequest;
import org.okaria.okhttp.response.SegmentResponse;
import org.okaria.okhttp.writer.SegmentWriter;
import org.okaria.setting.Properties;
import org.okaria.speed.SpeedMonitor;

import okhttp3.OkHttpClient;

public class SegmentClient extends Client implements SegmentResponse, StreamOrder, ThreadOrder  {
	SegmentWriter segmentWriter;
	ExecutorService executor;

	public SegmentClient(OkConfig config) {
		super(config);
		init();
	}
	public SegmentClient(OkHttpClient httpClient) {
		super(httpClient);
		init();
	}
	
	protected void init() {
		this.executor =  Executors.newCachedThreadPool();
		this.segmentWriter = new SegmentWriter() {};
	}
	
	@Override
	public List<Integer> downloadOrder(int rangeCount) {
		return StreamOrder.super.streamDownloadOrder(rangeCount);
	}

	@Override
	public boolean downloadTask(ItemMetaData placeHolder, int index, SpeedMonitor... monitors) {
		return SegmentResponse.super.downloadTask(placeHolder, index, monitors);
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
	public SegmentWriter getSegmentWriter() {
		return segmentWriter;
	}
	@Override
	public ExecutorService getExecutorService() {
		return executor;
	}

}
