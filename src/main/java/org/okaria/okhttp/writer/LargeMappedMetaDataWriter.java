package org.okaria.okhttp.writer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.segment.Segment;

public class LargeMappedMetaDataWriter extends ItemMetaData{

	private static class Pair{
		long start, limit; 
		int size;
		void initSize() {
			size = (int) (limit - start);
		}
	}
	
	private static class MappedData{
		int start;
		MappedByteBuffer mappedBuffer;
	}
	
	Map<Pair, MappedByteBuffer> mappedBuffers;
	public LargeMappedMetaDataWriter(Item item) {
		super(item);
		this.mappedBuffers = new HashMap<>();
		initMetaData();
	}
	
	private void initMetaData(){
		FileChannel  channel = raf.getChannel();
		long length = info.getFileLength();
		for (long pos = 0; pos < length; ) {
			Pair pair = new Pair();
			pair.start = pos;
			pos += Integer.MAX_VALUE;
			pair.limit = Math.min(pos, length);
			pair.initSize();
			pos = pair.limit;
			Log.trace(getClass(), "start/limit", pair.start + " <=> " + pair.limit + " <=> " +  pair.size);
			try {
				MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, pair.start, pair.size);
				Log.trace(getClass(), "create mapped byte buffer", buffer.toString());
				mappedBuffers.put(pair, buffer);
			} catch (IOException e) {
				Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
			}
		}
		
		
		
	}
	
	protected MappedData getMappedDataOfPosition(long startPositin) {
		for (Pair pair : mappedBuffers.keySet()) {
			if(startPositin >= pair.start & startPositin <= pair.limit) {
				MappedData data = new MappedData();
				data.start = (int) (startPositin - pair.start);
				data.mappedBuffer = mappedBuffers.get(pair);
				return data;
			}
		}
		return null;
	}
	protected void forceAllMappedByteBuffers() {
		mappedBuffers.forEach((d,m)->{m.force();});
	}
	
	
	@Override
	public void systemFlush() {
		if(segments.isEmpty()) return;
		Iterator<Segment> iterator =  segments.iterator();
		StringBuilder report = new StringBuilder();
		while (iterator.hasNext()) {
			Segment segment = (Segment) iterator.next();
			MappedData data = getMappedDataOfPosition(segment.start);
			try {
				data.mappedBuffer.position(data.start);
				data.mappedBuffer.put(segment.buffer);
				report.append(segment.toString());
			} catch (Exception e) {
				Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
			}
			
			releaseSegment(segment);
			iterator.remove();
		}
		forceAllMappedByteBuffers();
		if(report.length() > 0)
			Log.trace(getClass(), "flush segments", report.toString());
		//saveItem2CacheFile();
	}
	
	

}
