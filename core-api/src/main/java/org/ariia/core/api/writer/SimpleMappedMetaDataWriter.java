package org.ariia.core.api.writer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.segment.Segment;

public class SimpleMappedMetaDataWriter extends ItemMetaData {

	protected MappedByteBuffer mappedBuffer;
	protected FileChannel  channel;
	public SimpleMappedMetaDataWriter(Item item, Client client, Properties properties) {
		super(item, client, properties);
	}
	@Override
	protected void initMetaData() {
		channel = raf.getChannel();
		try {
			mappedBuffer = channel.map(MapMode.READ_WRITE, 0, raf.length());
		} catch (IOException e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
		}
	}
	
	@Override
	protected boolean writeSegment(Segment segment) {
		try {
			mappedBuffer.position((int)segment.start);
			while (segment.buffer.hasRemaining()) {
				mappedBuffer.put(segment.buffer);
			}
			return true;
		} catch (Exception e) {
			Log.error(getClass(), "flush data to file ", item.path() + '\n' + e);
			return false;
		}
	}
	
	@Override
	public void forceUpdate() {
		mappedBuffer.force();
	}

	
//	@Override
//	public void clearFile() {
//		mappedBuffer.position(0);
//		int segment = mappedBuffer.capacity() > 2028098 ? 2028098 : 1;
//		for (int pos = 0; pos < mappedBuffer.capacity(); pos += segment) {
//			mappedBuffer.put((byte)0);
//		}
//		mappedBuffer.force();
//	}
	
	
	@Override
	public void close() {
		mappedBuffer.force();
		super.close();
	}

}
