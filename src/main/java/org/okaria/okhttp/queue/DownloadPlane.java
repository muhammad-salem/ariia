package org.okaria.okhttp.queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.speed.SpeedMonitor;

public interface DownloadPlane {
	

	Future<?> downloadPart(ItemMetaData placeHolder, int index, SpeedMonitor... monitors);
	
	default List<Future<?>> download(ItemMetaData placeHolder, SpeedMonitor itemMonitor, SpeedMonitor... monitors) {
		return download(placeHolder, toArray(itemMonitor, monitors));
	}
	/**
	 * @param itemMonitor
	 * @param monitors
	 * @return
	 */
	default SpeedMonitor[] toArray(SpeedMonitor itemMonitor,
			SpeedMonitor... monitors) {
		SpeedMonitor[] allMonitors = Arrays.copyOf(monitors, monitors.length + 1);
		allMonitors[monitors.length] = itemMonitor;
		return allMonitors;
	}
	default List<Future<?>> download(ItemMetaData placeHolder, SpeedMonitor... monitors) {
		Item item = placeHolder.getItem();
		item.updateHeaders();
		if(item.isStreaming()) {
			Log.info(getClass(), "streaming...", item.getFilename());
		}
		else if(item.isFinish()) {
			Log.info(getClass(), "Download Complete", item.getFilename());
			return null;
		}
		List<Integer> indexs = downloadOrder(item.getRangeInfo().getRangeCount());
		List<Future<?>> futures = new LinkedList<Future<?>>();
		for (Integer index : indexs) {
			futures.add(downloadPart(placeHolder, index, monitors));
		}
		return futures;
	}
	
	default List<Integer> downloadOrder(int rangeCount){
		List<Integer> indexs = new ArrayList<>();
		for (int i = 0; i < rangeCount; i++) {
			indexs.add(i);
		}
		return indexs;
	}
	
	
	default Future<?> downloadPart(ItemMetaData placeHolder, int index, SpeedMonitor itemMonitor, SpeedMonitor... monitors){
		return downloadPart(placeHolder, index, toArray(itemMonitor, monitors));
	}
	
}
