package org.okaria.queue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.okaria.manager.Item;
import org.okaria.okhttp.ClientRequest;
import org.okaria.okhttp.ClientResponse;
import org.okaria.speed.SpeedMonitor;

public interface DownloadProcess extends ClientRequest, ClientResponse {
	
	Item item();					void item(Item item);
	ExecutorService executor();		void executor(ExecutorService executor);
	List<Future<?>> futures();
	

	/**
	 * 
	 * @param monitors
	 *            should be more than 1 last one is for all current threads
	 * 
	 */
	default void downloadItem(SpeedMonitor... monitors) {
		Item item = item();
		if (item.getRangeInfo().isFinish())
			return;
		if (monitors.length == 1) {
			for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
				downloadPart(index, monitors);
			}
		} else {
			for (int index = 0; index < item.getRangeInfo().getRangeCount(); index++) {
				downloadPart(index, monitors[monitors.length - 1], monitors[index]);
			}
		}

	}

	default void downloadPart( int index, SpeedMonitor... monitors) {
		Future<?> future = executor().submit(() -> {
			boolean finsh = false;
			while (!finsh) {
				finsh = downloadTask(item(), index, monitors);
			}
		});
		futures().add(future);
	}
	

}
