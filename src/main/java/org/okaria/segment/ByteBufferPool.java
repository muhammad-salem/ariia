package org.okaria.segment;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPool {
	
	private static final int[] MEMORY_LIMIT = new int[] {5, 7, 11, 13, 17, 19, 23 };
	private static int MEMORY_SIZE;
	
	private static final int BUFFER_SIZE = 8192; // 2048;
    private static ConcurrentLinkedQueue<ByteBuffer> MEMORY_QUEUE = new ConcurrentLinkedQueue<>();

    public static ByteBuffer acquire()
    {
        ByteBuffer buffer = MEMORY_QUEUE.poll();
        if (buffer == null) {
        	buffer = ByteBuffer.allocate(BUFFER_SIZE);
        	if(MEMORY_SIZE == 0) {
        		MEMORY_SIZE = MEMORY_LIMIT[0] * 1024 * 1024 / BUFFER_SIZE; 
        	}
        }
//        System.out.println("<==" + MEMORY_QUEUE.size());
        return buffer;
    }

    public static void release(ByteBuffer buffer)
    {
    	if(MEMORY_QUEUE.size() < MEMORY_SIZE) {
    		buffer.clear();
    		MEMORY_QUEUE.offer(buffer);	
    	}
//    	System.out.println("   " + MEMORY_QUEUE.size() + "==>");
//    		else {
//    		while (MEMORY_QUEUE.size() > MEMORY_SIZE) {
//				MEMORY_QUEUE.poll();
//			}
//    		System.gc();
//    	}
        
    }

    public static void clear()
    {
        MEMORY_QUEUE.clear();
    }
}
