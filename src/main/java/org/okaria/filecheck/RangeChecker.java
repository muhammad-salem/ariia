package org.okaria.filecheck;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;

public class RangeChecker implements Closeable {
	
	RandomAccessFile file;
	long chunkSize;
	public RangeChecker(String checkFile) {
		this(checkFile, 512*1024);
	}
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
		long pos;
		int temp ;
		
		long limit = file.length() - chunkSize;
		out.print("[\n");
		for ( pos = 0 ; pos < limit ; pos += chunkSize) {
			file.seek(pos);
			temp = file.read();
			if(temp == 0x00) {
				out.print("\t[\t" + pos + ",\t" + (long)(pos + chunkSize)  + " ],\n");
			}
		}
		file.seek(pos);
		temp = file.read();
		if(temp == 0x00) {
			out.print("\t[\t" + pos + ",\t" + (long)(file.length()-1) + " ]");
		} else {
			out.append("\t[\t0,\t0]");
		}
		out.print("\n]");
		out.close();

	}
	@Override
	public void close() throws IOException {
		file.close();
	}
}
