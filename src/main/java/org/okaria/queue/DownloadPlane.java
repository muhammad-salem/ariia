package org.okaria.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.speed.SpeedMonitor;

public interface DownloadPlane {
	
	default public List<Future<?>> download(Item item, SpeedMonitor... monitors) {
		item.updateHeaders();
		if(item.isStreaming()) {
			Log.info(getClass(), item.getFilename(), "streaming...");
		}
		else if(item.isFinish()) {
			Log.info(getClass(), item.getFilename(), "Download Complete");
			return null;
		}
		return downloadItem(item, monitors);
	}
	
	default public List<Future<?>> downloadItem(Item item, SpeedMonitor... monitors) {
		List<Future<?>> futures = new LinkedList<Future<?>>();
		for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
			futures.add(downloadPart(item, index, monitors));
		}
		return futures;
	}
	
	Future<?> downloadPart(Item item, int index, SpeedMonitor... monitors);
	
}
