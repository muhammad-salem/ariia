package org.okaria.segment;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.log.concurrent.Log;

public class ByteBufferPool {
	
	private static final int BUFFER_SIZE = 8192; // AS 8 KByte
	private static ConcurrentLinkedQueue<ByteBuffer> MEMORY_QUEUE = new ConcurrentLinkedQueue<>();
	
	
    public static ByteBuffer acquire()
    {
        ByteBuffer buffer = MEMORY_QUEUE.poll();
        if (buffer == null) {
        	buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
//        	buffer = ByteBuffer.allocate(BUFFER_SIZE);
        }
//      Log.info(ByteBufferPool.class, " memory queue", "allocateDirect <==" + MEMORY_QUEUE.size());
        return buffer;
    }

    public static void release(ByteBuffer buffer)
    {
    	buffer.clear();
    	MEMORY_QUEUE.offer(buffer);
    }

    public static void clear()
    {
        MEMORY_QUEUE.clear();
    }
}
