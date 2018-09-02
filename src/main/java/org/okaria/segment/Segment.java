package org.okaria.segment;

import java.nio.ByteBuffer;

public class Segment /* implements Comparable<Segment>*/{
	
	public int index;
	public long start;
	public ByteBuffer buffer;
	
	public Segment(int index) {
		this(index, 0l);
	}
	public Segment(int index, long start) {
		this.index = index;
		this.start = start;
		this.buffer = ByteBufferPool.acquire();
	}
	
	/**
	 * had to flip buffer before call this fun
	 * to make sure limit had been set to current position
	 * @return
	 */
	public Segment nextSegment() {
		return new Segment(index, start + buffer.limit());
	}
	
	public interface OfferSegment {
		void offerSegment(Segment segment);
		long startOfIndex(int index);
		long limitOfIndex(int index);
		default void releaseSegment(Segment segment) {
			ByteBufferPool.release(segment.buffer);
		}
	}
	
	@Override
	public String toString() {
		return "Segment[ index=" + index + " start=" + start 
				+ " buffer=(pos="+buffer.position()+" lim="+buffer.limit()+" cap="+buffer.capacity()+")]";
	}
//	@Override
//	public int compareTo(Segment otherSegment) {
//		if(this.index < otherSegment.index) return 1;
//		else if(this.index > otherSegment.index) return -1;
//		else return 0;
//	}
	
}
