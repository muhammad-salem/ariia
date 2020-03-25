package org.okaria.okhttp.writer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.okaria.range.RangeResponseHeader;
import org.okaria.segment.Segment;
import org.okaria.segment.Segment.OfferSegment;
import org.okaria.speed.SpeedMonitor;
import org.okaria.speed.net.MonitorInputStreamWrapper;

import okhttp3.Response;

public interface SegmentWriter {

	default void writeResponse
			(Response response, OfferSegment offerSegment,int index, SpeedMonitor... monitors) 
			throws IOException 
	{
				writeResponse(response, offerSegment, index, -1, monitors);
	}
	
	default void writeResponse(Response response, OfferSegment offerSegment,int index, long limit, SpeedMonitor... monitors) 
	throws IOException {
		long start = 0l;
		if (response.code() == 200) {
			start = 0;
			if(offerSegment.startOfIndex(index) != 0 ) throw new IOException("expected start of range = 0 as first byte to 'GET'"); //return;
		} else if (response.code() == 206) {
			RangeResponseHeader range = new RangeResponseHeader(
					response.header("Content-Range"));
			start = range.start;
		} else if (response.code() == 416) { // error state
			
			return;
		}
		
		
		InputStream source = MonitorInputStreamWrapper.wrap(response.body().byteStream(), monitors);
//		String encoding = response.header("Content-Encoding", "");
//		if( ! encoding.isEmpty()) {
//			if(encoding.equals("gzip")) {
//				source = new GZIPInputStream(source);
//			}
//		}
		
		
		ReadableByteChannel reader = Channels.newChannel(source);
		
		
		if(limit == -1) writeStream(reader, offerSegment, index, start);
		else			writeLimit (reader, offerSegment, index, start, limit);
		
	}
	

	default void writeStream(ReadableByteChannel reader, OfferSegment offerSegment, final int index, final long start) throws IOException {
		Segment segment = new Segment(index, start);
		ByteBuffer buffer = segment.buffer;
		long length = start;
		int len = 0;
		try {
			while (true) {
				if(buffer.hasRemaining()) {
					len = reader.read(buffer);
					if( len == -1) throw new IOException("the channel has reached end-of-stream.");
					length += len;
				}else {
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
		Segment segment = new Segment(index, start);
		ByteBuffer buffer = segment.buffer;
		long length = start;
		int len = 0;
		try {
			while (true) {
				if(buffer.hasRemaining()) {
					len = reader.read(buffer);
					if( len == -1) throw new IOException("the channel has reached end-of-stream.");
					length += len;
				}else {
					buffer.flip();
					offerSegment.offerSegment(segment);
					if(length >= limit) return;
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
