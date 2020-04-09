package org.ariia.core.api.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ariia.core.api.queue.StreamOrder;
import org.ariia.core.api.queue.ThreadOrder;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.response.SegmentDownloader;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.SegmentWriter;
import org.ariia.speed.SpeedMonitor;

public class SegmentClient extends Client implements StreamOrder, ThreadOrder  {

	SegmentDownloader segmentDownloader;
	
	public SegmentClient(int retries, ClientRequest clientRequest) {
		this(retries, clientRequest, Executors.newCachedThreadPool(),
				new SegmentDownloader(clientRequest, new SegmentWriter(){}) );
	}
	
	public SegmentClient(int retries, ClientRequest clientRequest, ExecutorService executor) {
		this(retries, clientRequest, executor,
				new SegmentDownloader(clientRequest, new SegmentWriter() {} ));
	}
	
	
	public SegmentClient(int retries, ClientRequest clientRequest, ExecutorService executor,
			SegmentWriter segmentWriter) {
		this(retries, clientRequest, executor,
				new SegmentDownloader(clientRequest, segmentWriter));
	}
	
	public SegmentClient(int retries, ClientRequest clientRequest, ExecutorService executor,
			SegmentDownloader segmentDownloader) {
		super(retries, clientRequest, executor);
		this.segmentDownloader = segmentDownloader;
	}
	
	@Override
	public boolean downloadTask(ItemMetaData metaData, int index,
			SpeedMonitor... monitors) {
		return segmentDownloader.downloadTask(metaData, index, monitors);
	}

}
