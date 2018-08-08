package org.okaria.okhttp.writer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Iterator;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.segment.Segment;

public class SimpleMappedMetaDataWriter extends ItemMetaData {

	protected MappedByteBuffer mappedBuffer;
	protected FileChannel  channel;
	public SimpleMappedMetaDataWriter(Item item) {
		super(item);
		initMetaData();
	}
	
	private void initMetaData(){
		channel = raf.getChannel();
		try {
			mappedBuffer = channel.map(MapMode.READ_WRITE, 0, raf.length());
		} catch (IOException e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
		}
	}

	@Override
	public synchronized void systemFlush() {
		if(segments.isEmpty()) return;
		
		Iterator<Segment> iterator =  segments.iterator();
		StringBuilder report = new StringBuilder();
		while (iterator.hasNext()) {
			Segment segment = (Segment) iterator.next();
			try {
				
				mappedBuffer.position((int)segment.start);
				mappedBuffer.put(segment.buffer);
				
				report.append(segment.toString());
			} catch (Exception e) {
				Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
//				if(raf != null) close();
//				initRandomAccessFile();
//				if(raf == null) return;
//				channel = raf.getChannel();
//				continue;		// could lead to infint loop 
			}
			
			releaseSegment(segment);
			iterator.remove();
		}
		mappedBuffer.force();
		if(report.length() > 0)
			Log.trace(getClass(), "flush segments", report.toString());
		//saveItem2CacheFile();
	}

}
