package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.segment.Segment;
import org.ariia.util.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class StreamMetaDataWriter extends ItemMetaData {

    private static Logger log = Logger.create(StreamMetaDataWriter.class);

    private FileChannel channel;

    public StreamMetaDataWriter(Item item, Client client, Properties properties) {
        super(item, client, properties);
    }

    @Override
    protected void initRandomAccessFile() {
        try {
            R.mkParentDir(item.path());
            raf = new RandomAccessFile(item.path(), "rw");
            raf.seek(info.startOfIndex(0));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e.toString());
        }
    }

    @Override
    public void initMetaData() {
        channel = raf.getChannel();
    }

    /**
     *
     */
    @Override
    public void forceUpdate() {
        try {
            channel.force(true);
        } catch (IOException e) {
            log.error("force update", "error force update to channel\n" + item.getFilename() + '\n' + e.getMessage());
        }
    }


    protected boolean writeSegment(Segment segment) {
        try {
            while (segment.buffer.hasRemaining()) {
                channel.write(segment.buffer);
            }
            return true;
        } catch (IOException e) {
            log.error("write segment", "error force update to channel\n" + item.getFilename() + '\n' + e.getMessage());
            return false;
        }

    }

//	@Override
//	public void clearFile() {}


    @Override
    public void close() {
        try {
            channel.force(true);
        } catch (IOException e) {
            log.error("close channel", "error force any updates of this channel's file "
                    + "\nto be written to the storage device that contains it \n"
                    + e.getMessage());
        }
        super.close();
    }

}
