package org.okaria.okhttp.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;


public interface ClientChannelWriter extends ClinetWriter{
	
	
	default void write(InputStream source, RandomAccessFile destination, long[] ranges, boolean stream) throws IOException {
		// Objects.requireNonNull(file, "file");		
		ReadableByteChannel reader =  Channels.newChannel(source);
		if(stream)
			writeStream(reader, destination.getChannel(), ranges);
		else
			write(reader, destination.getChannel(), ranges);
	}
	/**
	 * @param reader
	 * @param writer
	 * @param ranges
	 * @param buffer
	 * @throws IOException
	 */
	default void writeStream(ReadableByteChannel reader, FileChannel writer, long[] ranges )
			throws IOException {
		int count = 0;
		ByteBuffer buffer = ByteBuffer.allocate(RESPONSE_BUFFER);
		while ((count = reader.read(buffer)) != -1) {
			buffer.flip();
			writer.write(buffer);
			buffer.clear();
			addToRange(ranges, count);
		}
	}
	
	/**
	 * @param reader
	 * @param writer
	 * @param ranges
	 * @param buffer
	 * @throws IOException
	 */
	default void write(ReadableByteChannel reader, FileChannel writer, long[] ranges )
			throws IOException {
		int count = 0;
		ByteBuffer buffer = ByteBuffer.allocate(RESPONSE_BUFFER);
		while ((count = reader.read(buffer)) != -1) {
			buffer.flip();
			writer.write(buffer);
			buffer.clear();
			addToRange(ranges, count);
			
			/**
			 * stop read write operation intently
			 * in case of keep reading more than the given range
			 * when modify/update ranges 
			 * new will read until new modified range
			 */
			if(ranges[0] - ranges[1] >= 0) break;
		}
	}

}
