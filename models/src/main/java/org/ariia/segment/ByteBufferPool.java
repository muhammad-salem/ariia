package org.ariia.segment;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ByteBufferPool {

    //	private static final int BUFFER_SIZE = 1572864; // (int) 1.5f*1024*1024; // 1.5MB
    private static final int BUFFER_SIZE = 8 * 1024; // 8KB

    //	private static final int QUEUE_MAX_SIZE = 100;
    private static ConcurrentLinkedQueue<ByteBuffer> BUFFER_QUEUE = new ConcurrentLinkedQueue<>();

    public static ByteBuffer acquire() {
        return Objects.requireNonNullElseGet(BUFFER_QUEUE.poll(), () -> ByteBuffer.allocate(BUFFER_SIZE));
    }

    public static void release(ByteBuffer buffer) {
        offer(buffer);
    }

    private static void offer(ByteBuffer buffer) {
        buffer.clear();
        BUFFER_QUEUE.offer(buffer);
    }

    public static void clear() {
        BUFFER_QUEUE.clear();
    }
}
