package org.ariia.core.api.writer;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.segment.Segment;

public class ChannelMetaDataWriter extends ItemMetaData {


	protected FileChannel channel;
	public ChannelMetaDataWriter(Item item) {
		super(item);
	}
	
	@Override
	public void initMetaData() {
		channel = raf.getChannel();
	}
	
	@Override
	public void forceUpdate() {
		try {
			channel.force(true);
		} catch (IOException e) {
			Log.error(getClass(), "force update", "error force update to channel\n" + item.getFilename() + '\n' + e.getMessage());
		}
	}

	
	@Override
	protected boolean writeSegment(Segment segment) {
		try {
			channel.position(segment.start);
			while (segment.buffer.hasRemaining()) {
				channel.write(segment.buffer);
			}
			return true;
		} catch (IOException e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
			return false;
		}
		
	}


//	@Override
//	public void clearFile() {
//		try {
//			channel.position(0);
//			int segment = raf.length() > 2028098 ? 1 : 2048;
//			ByteBuffer buffer = ByteBuffer.allocate(segment);
//			for (int pos = 0; pos < raf.length(); pos += segment) {
//				channel.write(buffer);
//				buffer.flip();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void close() {
		try {
			channel.force(true);
		} catch (IOException e) {
			Log.error(getClass(), "close channel", "force any updates of this channel's file"
					+ "\nto be written to the storage device\n" 
					+ e.getMessage());
		}
		super.close();
	}
}
