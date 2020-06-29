package org.ariia.core.api.writer;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.items.Item;
import org.ariia.segment.Segment;
import org.ariia.speed.report.SpeedMonitor;

public class ItemMetaDataCompleteWrapper extends ItemMetaData {

    public ItemMetaDataCompleteWrapper(Item item, Client client, Properties properties) {
        super(item, client, properties);
    }

    @Override
    public void releaseSegment(Segment segment) {
    }

    @Override
    protected void initMetaData() {
    }

    @Override
    protected boolean writeSegment(Segment segment) {
        return false;
    }

    @Override
    public void forceUpdate() {
    }

    @Override
    protected void initRandomAccessFile() {
    }

    @Override
    public boolean isClose() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public void offerSegment(Segment segment) {
    }

    @Override
    public synchronized void systemFlush() {
    }

    @Override
    public int segmentSize() {
        return 0;
    }

    @Override
    public boolean isDownloading() {
        return false;
    }

    @Override
    public boolean allowSegmentWrite() {
        return false;
    }

    @Override
    public void checkCompleted() {
    }

    @Override
    public void checkWhileDownloading() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void initWaitQueue() {
    }

    @Override
    public boolean isDownloadListEmpty() {
        return true;
    }

    @Override
    public void startAndCheckDownloadQueue(SpeedMonitor... monitors) {
    }


}
