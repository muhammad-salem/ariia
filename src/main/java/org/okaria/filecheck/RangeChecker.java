package org.okaria.filecheck;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class RangeChecker implements Closeable {
	
	RandomAccessFile file;
	public RangeChecker(String checkFile) {
		try {
			file = new RandomAccessFile(checkFile, "r");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void channelrFormat(String outfile) throws IOException {
		file.seek(0);
		FileChannel channel = file.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(2048);
		PrintStream out = new PrintStream(outfile);
		byte zero = 0, tempByte;
		long start = -1;
		long end = -1;
		long pos = 0;

		out.append("[\n");
		while (channel.read(buffer) > 0) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				tempByte = buffer.get();
				if (tempByte == zero) {
					if (start == -1) {
						start = pos;
						end = pos;
						pos++;
					}else {
						end = pos;
						pos++;
					}
				} else {
//					if (end - start < 50) {
//						pos++;
//						continue;
//					} 
//					else 
					if (start != -1) {
						out.print("\t[\t" + start + ",\t" + end + " ],\n");
						start = -1;
						end = pos;
						pos++;
					}
				}
				
			}
			buffer.clear();
		}

		if (start != -1)
			out.print("\t[\t" + start + ",\t" + end + " ]\n]");
		else
			out.print("\n]");
		out.close();

	}
	
//	void channelFormat(String outfile) throws IOException {
//		file.seek(0);
//		FileChannel channel = file.getChannel();
//		ByteBuffer buffer = ByteBuffer.allocate(2048);
//		
//		byte zeroByte = 0, tempByte;
//		
//		ConcurrentLinkedQueue<Long> zeros = new ConcurrentLinkedQueue<>();
//		ConcurrentLinkedQueue<Long> nonZero = new ConcurrentLinkedQueue<>();
//		long pos = 0;
//		while (channel.read(buffer) > 0) {
//			buffer.flip();
//			while (buffer.hasRemaining()) {
//				tempByte = buffer.get();
//				if (tempByte == zeroByte) {
//					zeros.add(pos);
//				} else {
//					nonZero.add(pos);
//				}
//			pos++;	
//			}
//			buffer.clear();
//		}
//		
//		long start, limit;
//		limit = nonZero.poll();
//		while (! nonZero.isEmpty()) {
//			while ((start = zeros.peek()) < limit) {
//				
//			}
//			limit = nonZero.poll();
//		}
//		
//		
//		
//		
//		PrintStream out = new PrintStream(outfile);
//		out.append("[\n");
//		if (start != -1)
//			out.print("\t[\t" + start + ",\t" + end + " ]\n]");
//		else
//			out.print("\n]");
//		out.close();
//
//	}

	@Override
	public void close() throws IOException {
		file.close();
	}
}
