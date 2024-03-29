package org.ariia.core.api.queue;

import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.logging.Logger;
import org.network.speed.report.SpeedMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

public interface ItemDownloader {

    Logger log = Logger.create(ItemDownloader.class);

    Future<?> downloadPart(ItemMetaData metaData, int index, SpeedMonitor... monitors);

    default List<Future<?>> download(ItemMetaData metaData, SpeedMonitor itemMonitor, SpeedMonitor... monitors) {
        return download(metaData, toArray(itemMonitor, monitors));
    }

    /**
     * @param itemMonitor
     * @param monitors
     * @return
     */
    default SpeedMonitor[] toArray(SpeedMonitor itemMonitor, SpeedMonitor... monitors) {
        SpeedMonitor[] allMonitors = Arrays.copyOf(monitors, monitors.length + 1);
        allMonitors[monitors.length] = itemMonitor;
        return allMonitors;
    }

    default List<Future<?>> download(ItemMetaData metaData, SpeedMonitor... monitors) {
        var item = metaData.getItem();
        if (item.isStreaming()) {
            log.info("Streaming...", item.getFilename());
        } else if (item.isFinish()) {
            log.info("Download Complete", item.getFilename());
            return null;
        }
        var indexes = downloadOrder(item.getRangeInfo().getRangeCount());
        var futures = new LinkedList<Future<?>>();
        for (var index : indexes) {
            futures.add(downloadPart(metaData, index, monitors));
        }
        return futures;
    }

    default List<Integer> downloadOrder(int rangeCount) {
        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < rangeCount; i++) {
            indexes.add(i);
        }
        return indexes;
    }


    default Future<?> downloadPart(ItemMetaData metaData, int index, SpeedMonitor itemMonitor, SpeedMonitor... monitors) {
        return downloadPart(metaData, index, toArray(itemMonitor, monitors));
    }

}
