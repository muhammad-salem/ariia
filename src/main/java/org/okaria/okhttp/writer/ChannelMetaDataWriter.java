package org.okaria.okhttp.writer;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.segment.Segment;

public class ChannelMetaDataWriter extends ItemMetaData {


	protected FileChannel channel;
	public ChannelMetaDataWriter(Item item) {
		super(item);
	}

	@Override
	protected void flush(ConcurrentLinkedQueue<Segment> segmentQueue) {
		if(segmentQueue.isEmpty()) return;
		Iterator<Segment> iterator =  segmentQueue.iterator();
		StringBuilder report = new StringBuilder();
		while (iterator.hasNext()) {
			Segment segment = (Segment) iterator.next();
			try {
				raf.seek(segment.start);
				channel = raf.getChannel();
				while (segment.buffer.hasRemaining()) {
					channel.write(segment.buffer);
				}
				report.append(segment.toString());
				report.append('\n');
			} catch (IOException e) {
				Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
				if(raf != null) close();
				initRandomAccessFile();
				if(raf == null) return; 
				continue;		// could lead to infint loop 
			}
			
			releaseSegment(segment);
			iterator.remove();
		}
		if(report.length() > 0)
			Log.trace(getClass(), "flush segments", report.toString());
		//saveItem2CacheFile();
	}

}
