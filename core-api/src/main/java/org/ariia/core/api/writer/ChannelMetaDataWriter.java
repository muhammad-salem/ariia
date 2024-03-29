package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.segment.Segment;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class ChannelMetaDataWriter extends ItemMetaData {

    private static Logger log = Logger.create(ChannelMetaDataWriter.class);

    protected FileChannel channel;

    public ChannelMetaDataWriter(Item item, Client client, Properties properties) {
        super(item, client, properties);
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
            log.error("force update", "error force update to channel\n" + item.getFilename() + '\n' + e.getMessage());
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
            log.error(e.getClass().getSimpleName(), e.getMessage());
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
            log.error("close channel", "force any updates of this channel's file"
                    + "\nto be written to the storage device\n"
                    + e.getMessage());
        }
        super.close();
    }
}
