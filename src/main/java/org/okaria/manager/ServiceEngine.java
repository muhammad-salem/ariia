package org.okaria.manager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.okaria.R;
import org.okaria.Utils;
import org.okaria.okhttp.OkClient;
import org.okaria.range.RangeInfoMonitor;
import org.okaria.speed.SpeedMonitor;

public class ServiceEngine {

	public final static int MAX_THREAd_POOL = 40;
	public final static int MAX_ACTIVE_DOWNLOAD_POOL = 5;
	public final static int SCHEDULE_TIME = 2;
	public final static int SCHEDULE_POOL = 5;

	ScheduledExecutorService cheduledService;
	ExecutorService downloadservice;

	private LinkedList<ItemEngine> downloadItems;
	private LinkedList<ItemEngine> queuItems;
	private LinkedList<Future<?>> futures;

	OkClient client;
	SpeedMonitor totalMonitor;

	int maxThreadPool;
	int maxActiveDownloadPool;

	boolean downloadPoolAvaliable;

	public ServiceEngine() {
		this(MAX_THREAd_POOL, MAX_ACTIVE_DOWNLOAD_POOL);
	}

	public ServiceEngine(int maxThreadPool, int maxActiveDownloadPool) {
		this.maxThreadPool = maxThreadPool;
		this.maxActiveDownloadPool = maxActiveDownloadPool;
		cheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
		downloadservice = Executors.newFixedThreadPool(maxThreadPool);
		downloadItems = new LinkedList<ItemEngine>();
		queuItems = new LinkedList<ItemEngine>();
		futures = new LinkedList<Future<?>>();
		downloadPoolAvaliable = true;
		totalMonitor = new RangeInfoMonitor();
	}

	public void shutdownEngine() {
		downloadservice.shutdown();
		cheduledService.shutdown();

	}

	public void startEngine() {
		// for each 2 second
		cheduledService.scheduleWithFixedDelay(this::prepareItem, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::runDownload, 2, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::checkFinish, 3, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::saveData, 4, SCHEDULE_TIME, TimeUnit.SECONDS);
		cheduledService.scheduleWithFixedDelay(this::stopDownload, 5, SCHEDULE_TIME, TimeUnit.SECONDS);

		cheduledService.scheduleWithFixedDelay(this::printSpeedReport, 5, 1, TimeUnit.SECONDS);
	}

	private void prepareItem() {
		if (downloadPoolAvaliable) {
			ItemEngine item = queuItems.poll();
			downloadItems.add(item);
		}
	}

	private void runDownload() {

		if (downloadItems.size() == MAX_ACTIVE_DOWNLOAD_POOL)
			return;
		for (ItemEngine itemEngine : downloadItems) {

			client.downloadItem(itemEngine.item,
					(SpeedMonitor[]) Arrays.asList(totalMonitor, itemEngine.monitors).toArray());

		}
	}

	private void stopDownload() {

	}

	private void checkFinish() {
		for (ItemEngine itemEngine : downloadItems) {
			if (itemEngine.item.getRangeInfo().isFinish()) {
				downloadItems.remove(itemEngine);
				downloadPoolAvaliable = true;
			}
		}
	}

	private void printSpeedReport() {
		totalMonitor.demondSpeedNow();

	}

	private void saveData() {
		for (ItemEngine itemEngine : downloadItems) {
			Utils.toJsonFile(R.getConfigPath(itemEngine.item.getFilename() + ".json"), itemEngine.item.getRangeInfo());
		}
	}

	public int addItem(ItemEngine item) {
		if (queuItems.add(item))
			return queuItems.indexOf(item);
		return -1;
	}

	public boolean reomveItem(ItemEngine item) {
		if (downloadItems.contains(item)) {
			stopDownload(item);
		}
		return queuItems.remove(item) | downloadItems.remove(item);
	}

	private void stopDownload(ItemEngine item) {

	}

	// private void startDownload(ItemEngine item) {
	//
	// }

	public ItemEngine reomveItem(int index) {
		return queuItems.remove(index);
	}

	public boolean isDownloading(int index) {
		if (futures.get(index) != null && !futures.get(index).isCancelled())
			return true;
		return false;
	}

}
