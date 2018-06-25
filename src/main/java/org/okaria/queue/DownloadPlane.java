package org.okaria.queue;

import org.okaria.manager.Item;
import org.okaria.speed.SpeedMonitor;

public interface DownloadPlane {
	
	default public void download(Item item, SpeedMonitor... monitors) {
		if(item.getRangeInfo().isFinish()) return;
		downloadItem(item, monitors);
	}
	
	default public void downloadItem(Item item, SpeedMonitor... monitors) {
		for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
			downloadPart(item, index, monitors);
		}
	}
	
	void downloadPart(Item item, int index, SpeedMonitor... monitors);
//	default void downloadPart(Item item, int index, SpeedMonitor... monitors) {
//		
//	}
}
