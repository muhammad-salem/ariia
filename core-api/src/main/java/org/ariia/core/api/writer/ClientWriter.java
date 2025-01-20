package org.ariia.core.api.writer;


import org.network.speed.net.MonitorInputStreamWrapper;
import org.network.speed.report.SpeedMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public interface ClientWriter {

    int RESPONSE_BUFFER = 2048;

    void write(InputStream source, RandomAccessFile destination, long[] ranges) throws IOException;

    default void writeResponse(InputStream source, RandomAccessFile destination, long[] ranges, SpeedMonitor... monitors)
            throws IOException {
        source = MonitorInputStreamWrapper.wrap(source, monitors);
        write(source, destination, ranges);
    }


    default void addToRange(long[] ranges, long count) {
        ranges[0] += count;
    }
}
