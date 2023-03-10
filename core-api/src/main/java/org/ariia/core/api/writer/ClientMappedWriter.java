package org.ariia.core.api.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.ReadableByteChannel;


public interface ClientMappedWriter extends ClinetWriter {


    default void write(InputStream source, RandomAccessFile destination, long[] ranges, boolean stream) throws IOException {
        // Objects.requireNonNull(file, "file");

        var writer = destination.getChannel().map(MapMode.READ_WRITE, ranges[0], ranges[1] - ranges[0]);
        var reader = Channels.newChannel(source);
        if (stream) {
            writeStream(reader, writer, ranges);
        } else {
            write(reader, writer, ranges);
        }
        writer.force();
    }

    /**
     *
     * @param reader
     * @param writer
     * @param ranges
     * @throws IOException
     */
    default void writeStream(ReadableByteChannel reader, MappedByteBuffer writer, long[] ranges) throws IOException {
        int count = 0;
        var buffer = ByteBuffer.allocate(RESPONSE_BUFFER);
        try {
            while ((count = reader.read(buffer)) != -1) {
                buffer.flip();
                writer.put(buffer);
                buffer.clear();
                addToRange(ranges, count);
            }
        } catch (IOException e) {
            writer.force();
            throw e;
        }
    }

    /**
     *
     * @param reader
     * @param writer
     * @param ranges
     * @throws IOException
     */
    default void write(ReadableByteChannel reader, MappedByteBuffer writer, long[] ranges)
            throws IOException {
        int count = 0;
        var buffer = ByteBuffer.allocate(RESPONSE_BUFFER);
        try {
            while ((count = reader.read(buffer)) != -1) {
                buffer.flip();
                writer.put(buffer);
                buffer.clear();
                addToRange(ranges, count);

                /**
                 * stop read write operation intently
                 * in case of keep reading more than the given range
                 * when modify/update ranges 
                 * new will read until new modified range
                 */
                if (ranges[0] - ranges[1] >= 0) break;
            }
        } catch (IOException e) {
            writer.force();
            throw e;
        }
    }
}
