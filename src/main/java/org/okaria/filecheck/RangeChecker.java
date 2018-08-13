package org.okaria.filecheck;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class RangeChecker implements Closeable {
	
	RandomAccessFile file;
	long chunkSize;
	public RangeChecker(String checkFile, long chunkSize) {
		try {
			file = new RandomAccessFile(checkFile, "r");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.chunkSize = chunkSize;
	}
	void channelFormat(String outfile) throws IOException {
		PrintStream out = new PrintStream(outfile);
		long pos = 0;
		int temp ;
		
		long limit = file.length() - chunkSize;
		out.print("[\n");
		for ( ; pos < limit ; pos += chunkSize) {
			file.seek(pos);
			temp = file.read();
			if(temp == 0x00) {
				out.print("\t[\t" + pos + ",\t" + (long)(pos + chunkSize)  + " ],\n");
			}
		}
		file.seek(pos);
		temp = file.read();
		if(temp == 0x00) {
			out.print("\t[\t" + pos + ",\t" + (long)(file.length()) + " ]");
		}else {
			out.append("[0,0]");
		}
		out.print("\n]");
		out.close();

	}
	@Override
	public void close() throws IOException {
		file.close();
	}
}
