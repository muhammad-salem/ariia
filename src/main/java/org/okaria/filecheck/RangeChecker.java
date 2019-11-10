package org.okaria.filecheck;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	Map<Long, Long> channelFormat() throws IOException {
		long pos;
		int temp ;
		Map<Long, Long> result = new HashMap<Long, Long>();
		long limit = file.length() - chunkSize;
		for ( pos = 0 ; pos < limit ; pos += chunkSize) {
			file.seek(pos);
			temp = file.read();
			if(temp == 0x00) {
				result.put(pos, (long)(pos + chunkSize));
			}
		}
		file.seek(pos);
		temp = file.read();
		if(temp == 0x00) {
			result.put(pos, (long)(file.length()-1));
		}
		return result;
	}
	
	long[][] channelFormatLong() throws IOException {
		Map<Long, Long> r = channelFormat();
		Iterator<Long> iterator =  r.keySet().iterator();
		long[][] data = new long[r.size()][2];
		for (int i = 0; i < data.length; i++) {
			data[i][0] = iterator.next();
			data[i][1] = r.get(data[i][0]);
		}
		return data;
	}
	
	
	@Override
	public void close() throws IOException {
		file.close();
	}
}
