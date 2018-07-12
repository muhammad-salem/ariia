package org.okaria.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.log.Log;
import org.okaria.manager.Item;
import org.okaria.speed.SpeedMonitor;

public interface DownloadPlane {
	
	default public List<Future<?>> download(Item item, SpeedMonitor... monitors) {
		item.updateHeaders();
		if(item.getRangeInfo().isFinish()) {
			Log.info(getClass(), item.getFilename(), "Download Complete");
			return null;
		}
		return downloadItem(item, monitors);
	}
	
	default public List<Future<?>> downloadItem(Item item, SpeedMonitor... monitors) {
		List<Future<?>> futures = new LinkedList<Future<?>>();
		for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
			futures .add(downloadPart(item, index, monitors));
		}
		return futures;
	}
	
	Future<?> downloadPart(Item item, int index, SpeedMonitor... monitors);
//	default void downloadPart(Item item, int index, SpeedMonitor... monitors) {
//		
//	}
}
