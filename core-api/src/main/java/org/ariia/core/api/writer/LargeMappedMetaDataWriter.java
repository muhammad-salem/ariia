package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.segment.Segment;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

public class LargeMappedMetaDataWriter extends ItemMetaData {

    private static Logger log = Logger.create(LargeMappedMetaDataWriter.class);

    Map<Pair, MappedByteBuffer> mappedBuffers;

    public LargeMappedMetaDataWriter(Item item, Client client, Properties properties) {
        super(item, client, properties);
    }

    @Override
    protected void initMetaData() {
        this.mappedBuffers = new HashMap<>();
        var channel = raf.getChannel();
        long length = info.getFileLength();
        for (long pos = 0; pos < length; ) {
            var pair = new Pair();
            pair.start = pos;
            pos += Integer.MAX_VALUE;
            pair.limit = Math.min(pos, length);
            pair.initSize();
            pos = pair.limit;
            log.trace("Pair ", pair.toString());
            try {
                var buffer = channel.map(MapMode.READ_WRITE, pair.start, pair.size);
                log.trace("create mapped byte buffer", buffer.toString());
                mappedBuffers.put(pair, buffer);
            } catch (IOException e) {
                log.error(e.getClass().getSimpleName(), e.getMessage());
            }
        }


    }

    protected MappedData getMappedDataOfPosition(long startPositin) {
        for (var pair : mappedBuffers.keySet()) {
            if (startPositin >= pair.start & startPositin <= pair.limit) {
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
        mappedBuffers.forEach((d, m) -> {
            m.force();
        });
    }

    /**
     *
     * @param segment
     * @return
     */
    protected boolean writeSegment(Segment segment) {
        var data = getMappedDataOfPosition(segment.start);
        if (data == null) return false;
        try {
            data.mappedBuffer.position(data.start);
            while (segment.buffer.hasRemaining()) {
                data.mappedBuffer.put(segment.buffer);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    /**
     * @param buffer
     */
    protected void clearMapped(MappedByteBuffer buffer) {
        buffer.position(0);
        int segment = buffer.capacity() > 2028098 ? 2028098 : 1;
        for (int pos = 0; pos < buffer.capacity(); pos += segment) {
            buffer.put((byte) 0);
        }
    }

    @Override
    public void close() {
        forceUpdate();
        super.close();
    }

    private static class Pair {
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

    private static class MappedData {
        int start;
        MappedByteBuffer mappedBuffer;
    }

}
