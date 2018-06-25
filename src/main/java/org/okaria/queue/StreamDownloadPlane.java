package org.okaria.queue;

import org.okaria.manager.Item;
import org.okaria.speed.SpeedMonitor;

public interface StreamDownloadPlane extends DownloadPlane {

	@Override
	default public void downloadItem(Item item, SpeedMonitor... monitors) {
		downloadPart(item, item.getRangeInfo().getRangeCount()-1, monitors);
		for (int index = 0; index < item.getRangeInfo().getRangeCount()-1; index++) {
			downloadPart(item, index, monitors);
		}
	}
}
