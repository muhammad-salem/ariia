package org.okaria.segment;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPool {
	private static final int BUFFER_SIZE = 2048; // XXX: Is this ideal?
    private static ConcurrentLinkedQueue<ByteBuffer> pool = new ConcurrentLinkedQueue<>();

    public static ByteBuffer acquire()
    {
        ByteBuffer buffer = pool.poll();
        if (buffer == null)
            //buffer = ByteBuffer.allocateDirect(BUFFER_SIZE); // Using DirectBuffer for zero-copy
            buffer = ByteBuffer.allocate(BUFFER_SIZE); // Using DirectBuffer for zero-copy
        return buffer;
    }

    public static void release(ByteBuffer buffer)
    {
        buffer.clear();
//        if(pool.size() < Properties.MAX_BUFFER_POOL)
        pool.offer(buffer);
    }

    public static void clear()
    {
        pool.clear();
    }
}
