package org.okaria.queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.okaria.manager.Item;
import org.okaria.speed.SpeedMonitor;

public interface StreamDownloadPlane extends DownloadPlane {

	@Override
	default public List<Future<?>> downloadItem(Item item, SpeedMonitor... monitors) {
		List<Future<?>> futures = new LinkedList<Future<?>>();
		futures.add(downloadPart(item, item.getRangeInfo().getRangeCount()-1, monitors));
		for (int index = 0; index < item.getRangeInfo().getRangeCount()-1; index++) {
			futures.add(downloadPart(item, index, monitors));
		}
		return futures;
	}
}
