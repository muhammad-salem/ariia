package org.ariia.core.api.writer;

import org.ariia.segment.Segment;
import org.ariia.segment.Segment.OfferSegment;
import org.network.speed.net.MonitorInputStreamWrapper;
import org.network.speed.report.SpeedMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public interface SegmentWriter {

    default void writeResponse(
            InputStream stream, OfferSegment offerSegment,
            int index, long start, long limit,
            SpeedMonitor... monitors)
            throws IOException {

        var source = MonitorInputStreamWrapper.wrap(stream, monitors);
        var reader = Channels.newChannel(source);

        if (limit == -1) {
            writeStream(reader, offerSegment, index, start);
        } else {
            writeLimit(reader, offerSegment, index, start, limit);
        }

    }

    default void writeStream(ReadableByteChannel reader, OfferSegment offerSegment, final int index, final long start) throws IOException {
        var segment = new Segment(index, start);
        var buffer = segment.buffer;
        long length = start;
        int len = 0;
        try {
            while (offerSegment.allowSegmentWrite()) {
                if (buffer.hasRemaining()) {
                    len = reader.read(buffer);
                    if (len == -1) throw new IOException("the channel has reached end-of-stream.");
                    length += len;
                } else {
                    buffer.flip();
                    offerSegment.offerSegment(segment);
                    segment = new Segment(index, length);
                    buffer = segment.buffer;
                }
            }
        } catch (IOException e) {
            buffer.flip();
            offerSegment.offerSegment(segment);
            throw e;
        }

    }

    default void writeLimit(ReadableByteChannel reader, OfferSegment offerSegment, final int index, final long start, final long limit) throws IOException {
        var segment = new Segment(index, start);
        var buffer = segment.buffer;
        long length = start;
        int len = 0;
        try {
            while (offerSegment.allowSegmentWrite()) {
                if (buffer.hasRemaining()) {
                    len = reader.read(buffer);
                    if (len == -1) throw new IOException("the channel has reached end-of-stream.");
                    length += len;
                } else {
                    buffer.flip();
                    offerSegment.offerSegment(segment);
                    if (length >= limit) return;
                    segment = new Segment(index, length);
                    buffer = segment.buffer;
                }
            }
        } catch (IOException e) {
            buffer.flip();
            offerSegment.offerSegment(segment);
            throw e;
        }

    }
}
