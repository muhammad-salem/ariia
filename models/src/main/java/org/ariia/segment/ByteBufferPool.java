package org.ariia.segment;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

//import org.log.concurrent.Log;

public class ByteBufferPool {
	
//	private static final int BUFFER_SIZE = 1572864; // (int) 1.5f*1024*1024; // 1.5MB
	private static final int BUFFER_SIZE = 8*1024; // 8KB
	
//	private static final int QUEUE_MAX_SIZE = 100;
	private static ConcurrentLinkedQueue<ByteBuffer> BUFFER_QUEUE = new ConcurrentLinkedQueue<>();
	
	public static ByteBuffer acquire() {
		ByteBuffer buffer = BUFFER_QUEUE.poll();
		if (buffer == null) {
//			buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		    buffer = ByteBuffer.allocate(BUFFER_SIZE);
		}
//		else {
//		    System.out.println("BUFFER_QUEUE.size : " + BUFFER_QUEUE.size());
//		}
//		Log.info(ByteBufferPool.class, "memory queue", "BUFFER_QUEUE.size : " + BUFFER_QUEUE.size() );
		return buffer;
	}

	public static void release(ByteBuffer buffer) {
//		if (BUFFER_QUEUE.size() < QUEUE_MAX_SIZE){
			offer(buffer);
//		}
		
	}

	private static void offer(ByteBuffer buffer) {
		buffer.clear();
		BUFFER_QUEUE.offer(buffer);
	}

	public static void clear() {
		BUFFER_QUEUE.clear();
	}
}
