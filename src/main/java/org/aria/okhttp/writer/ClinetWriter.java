package org.aria.okhttp.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.aria.range.RangeResponseHeader;
import org.aria.speed.SpeedMonitor;
import org.aria.speed.net.MonitorInputStreamWrapper;

import okhttp3.Response;

public interface ClinetWriter {
	
	int RESPONSE_BUFFER = 2048;
	
	
	default void writeResponse(Response response, RandomAccessFile destination, long[] ranges, SpeedMonitor... monitors)
			throws IOException {
		if (response.code() == 200) {
			destination.seek(0);
			if(ranges[0] != 0 ) return;
		} else if (response.code() == 206) {
			RangeResponseHeader range = new RangeResponseHeader(response.header("Content-Range"));
			destination.seek(range.start);
		}else if (response.code() == 416) {			// error state
			return;
		}
		
		InputStream source = MonitorInputStreamWrapper.wrap(response.body().byteStream(), monitors);
		
//		String encoding = response.header("Content-Encoding", "");
//		if( ! encoding.isEmpty()) {
//			if(encoding.equals("gzip")) {
//				source = new GZIPInputStream(source);
//			}
//		}
		write(source, destination, ranges, false);
	}
	
	default void write(InputStream source, RandomAccessFile destination, long[] ranges, SpeedMonitor... monitors)
			throws IOException {
		
		write(source, destination, ranges, true, monitors);
	}
	
	default void write(InputStream source, RandomAccessFile destination, long[] ranges, boolean stream, SpeedMonitor... monitors)
			throws IOException {
		source = MonitorInputStreamWrapper.wrap(source, monitors);
		write( source, destination,ranges, false);
	}
	
	void write(InputStream source, RandomAccessFile destination, long[] ranges, boolean stream) throws IOException;
	

	default void addToRange(long[] ranges, long count) {
		ranges[0] += count;
	}
}
