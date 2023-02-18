package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.logging.Logger;
import org.ariia.monitors.RangeReport;
import org.ariia.range.RangeUtil;
import org.ariia.segment.Segment;
import org.ariia.segment.Segment.OfferSegment;
import org.ariia.util.R;
import org.network.speed.report.SpeedMonitor;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

//import org.ariia.core.api.queue.ItemDownloader;

public abstract class ItemMetaData implements OfferSegment, Closeable {

    private static Logger log = Logger.create(ItemMetaData.class);

    protected Item item;
    protected RangeUtil info;

    protected boolean downloading = false;
    protected RangeReport rangeReport;

    protected RandomAccessFile raf;
    protected Properties properties;
    protected Client client;
    private ConcurrentLinkedQueue<Segment> segments;
    private HashMap<Integer, Future<?>> downloadMap;
    private Queue<Integer> waitQueue;

    public ItemMetaData(Item item, Client client, Properties properties) {
        this.item = item;
        this.client = client;
        this.info = item.getRangeInfo();
        this.rangeReport = new RangeReport(info, item.getFilename());
        this.segments = new ConcurrentLinkedQueue<>();
        this.downloadMap = new HashMap<>();
        this.waitQueue = new LinkedList<>();
        this.properties = properties;
        initRandomAccessFile();
        initMetaData();
    }

    protected abstract void initMetaData();
    // public abstract void clearFile();

    protected abstract boolean writeSegment(Segment segment);

    public abstract void forceUpdate();

    /**
     * @param item
     */
    protected void initRandomAccessFile() {
        try {
            R.mkParentDir(item.path());
            raf = new RandomAccessFile(item.path(), "rw");
            raf.setLength(info.getFileLength());
        } catch (IOException e) {
            log.error(e.getMessage(), e.toString());
        }

    }

    public boolean isClose() {
        return !raf.getChannel().isOpen();
    }

    public boolean isOpen() {
        return raf.getChannel().isOpen();
    }

    @Override
    public void close() {
        try {
            raf.close();
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void offerSegment(Segment segment) {
        if (segment.buffer.remaining() == 0) {
            releaseSegment(segment);
            return;
        }
        segments.add(segment);
        info.addStartOfIndex(segment.index, segment.buffer.limit());
    }

    public synchronized void systemFlush() {
        if (segments.isEmpty())
            return;
        flush(segments);
        System.out.print(".");
    }

    private void flush(Queue<Segment> queue) {
        Segment segment;
        int writtenSegmentCount = 0;
        while (!queue.isEmpty()) {
            segment = queue.peek();
            if (segment == null)
                break;
            if (writeSegment(segment)) {
                writtenSegmentCount++;
                queue.poll();
                releaseSegment(segment);
            } else {
                // check raf is opened
                if (raf == null || isClose()) {
                    initRandomAccessFile();
                    initMetaData();
                }
            }
        }

        // saveItem2CacheFile();
        forceUpdate();

        if (writtenSegmentCount > 0) {
            log.trace("flush segments",
                    String.format("File Name: %s\nWritten Segment Count: %s", item.getFilename(), writtenSegmentCount));
        }
    }

    public int segmentSize() {
        return segments.size();
    }

    @Override
    public long startOfIndex(int index) {
        return info.startOfIndex(index);
    }

    @Override
    public long limitOfIndex(int index) {
        return info.limitOfIndex(index);
    }

    public Item getItem() {
        return item;
    }

    public Properties getProperties() {
        return properties;
    }

    public RangeUtil getRangeInfo() {
        return info;
    }

    public RangeReport getRangeReport() {
        return rangeReport;
    }

    public boolean isDownloading() {
        return downloading;
    }

    @Override
    public boolean allowSegmentWrite() {
        return downloading;
    }

    public void checkCompleted() {
        Iterator<Entry<Integer, Future<?>>> iterator = downloadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Future<?>> entry = iterator.next();
            if (info.isFinish(entry.getKey())) {
                iterator.remove();
            } else {
                waitQueue.add(entry.getKey());
                iterator.remove();
            }
        }
    }

    public void checkWhileDownloading() {
        Iterator<Entry<Integer, Future<?>>> iterator = downloadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Future<?>> entry = iterator.next();
            boolean isFinish = info.isFinish(entry.getKey());
            boolean isDone = entry.getValue().isDone();
            if (isDone && isFinish) {
                iterator.remove();
            } else if (isDone && !isFinish) {
                waitQueue.add(entry.getKey());
                iterator.remove();
            }
        }
    }

    public void pause() {
        downloading = false;
        checkCompleted();
    }

    public void initWaitQueue() {
        int count = info.getRangeCount();
        if (count == 0)
            return;
        waitQueue.clear();
        for (int index = 0; index < count; index++) {
            if (!info.isFinish(index)) {
                waitQueue.add(index);
            }
        }
    }

    public boolean isDownloadMapEmpty() {
        return downloadMap.isEmpty();
    }

    public void startAndCheckDownloadQueue(SpeedMonitor... monitors) {
        if (waitQueue.isEmpty()) {
            return;
        }
        downloading = true;
        while (downloadMap.size() < properties.getRangePoolNum() & !waitQueue.isEmpty()) {
            Integer index = waitQueue.poll();
            if (index == null)
                break;
            else {
                if (info.isFinish(index)) {
                    continue;
                }
                Future<?> downladProcess = client.downloadPart(this, index, rangeReport.getMonitor(), monitors);
                downloadMap.put(index, downladProcess);
            }
        }
    }

}
