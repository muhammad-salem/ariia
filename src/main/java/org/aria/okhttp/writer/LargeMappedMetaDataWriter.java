package org.aria.okhttp.writer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import org.aria.logging.Log;
import org.aria.manager.Item;
import org.aria.manager.ItemMetaData;
import org.aria.segment.Segment;

public class LargeMappedMetaDataWriter extends ItemMetaData{

	private static class Pair{
		long start, limit; 
		long size;
		void initSize() {
			size = limit - start;
		}
		@Override
		public String toString() {
			return start + " : " + limit + " -> " + size;
		}
	}
	
	private static class MappedData{
		int start;
		MappedByteBuffer mappedBuffer;
	}
	
	Map<Pair, MappedByteBuffer> mappedBuffers;
	public LargeMappedMetaDataWriter(Item item) {
		super(item);
	}
	
	@Override
	protected void initMetaData() {
		this.mappedBuffers = new HashMap<>();
		FileChannel  channel = raf.getChannel();
		long length = info.getFileLength();
		for (long pos = 0; pos < length; ) {
			Pair pair = new Pair();
			pair.start = pos;
			pos += Integer.MAX_VALUE;
			pair.limit = Math.min(pos, length);
			pair.initSize();
			pos = pair.limit;
			Log.trace(getClass(), "Pair ", pair.toString());
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
	
	@Override
	public void forceUpdate() {
		mappedBuffers.forEach((d,m)->{m.force();});
	}
	

	/**
	 * @param report
	 * @param segment
	 * @return 
	 */
	protected boolean writeSegment(Segment segment) {
		MappedData data = getMappedDataOfPosition(segment.start);
		if(data == null) return false;
		try {
			data.mappedBuffer.position(data.start);
			while (segment.buffer.hasRemaining()) {
			data.mappedBuffer.put(segment.buffer);
			}
			return true;
		} catch (Exception e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
			return false;
		}
	}
	
	
	
//	/**
//	 * will wipe data in this file 
//	 * all the data will be zero
//	 */
//	@Override
//	public void clearFile() {
//		synchronized(mappedBuffers) {
//			mappedBuffers.forEach((d,m)->{clearMapped(m);});
//			forceUpdate();
//		}
//	}

	/**
	 * @param buffer
	 */
	protected void clearMapped(MappedByteBuffer buffer) {
		buffer.position(0);
		int segment = buffer.capacity() > 2028098 ? 2028098 : 1;
		for (int pos = 0; pos < buffer.capacity(); pos += segment) {
			buffer.put((byte)0);
		}
	}
	
	
	@Override
	public void close() {
		forceUpdate();
		super.close();
	}

}
