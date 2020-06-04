package org.ariia.core.api.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ariia.config.Properties;
import org.ariia.core.api.queue.StreamOrder;
import org.ariia.core.api.queue.ThreadOrder;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.response.SegmentDownloader;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.SegmentWriter;
import org.ariia.speed.SpeedMonitor;

public class SegmentClient extends Client implements StreamOrder, ThreadOrder  {

	SegmentDownloader segmentDownloader;
	
	public SegmentClient(Properties properties, ClientRequest clientRequest) {
		this(properties, clientRequest, Executors.newCachedThreadPool(),
				new SegmentDownloader(clientRequest, new SegmentWriter(){}) );
	}
	
	public SegmentClient(Properties properties, ClientRequest clientRequest, ExecutorService executor) {
		this(properties, clientRequest, executor,
				new SegmentDownloader(clientRequest, new SegmentWriter() {} ));
	}
	
	
	public SegmentClient(Properties properties, ClientRequest clientRequest, ExecutorService executor,
			SegmentWriter segmentWriter) {
		this(properties, clientRequest, executor,
				new SegmentDownloader(clientRequest, segmentWriter));
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
