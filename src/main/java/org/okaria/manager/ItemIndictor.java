package org.okaria.manager;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import org.log.Log;
import org.okaria.R;
import org.okaria.Utils;
import org.okaria.queue.DownloadPlane;
import org.okaria.range.RangeInfo;
import org.okaria.speed.SpeedMonitor;

public class ItemIndictor {
	
	private Item item;
	private File cacheFile;
	private List<Future<?>> futures;
	
	

	private boolean downloading = false;
	
	
	public ItemIndictor(Item item) {
		this.item = item;
		this.cacheFile = R.getConfigFile(Utils.jsonFileName(item.url()));
	}
	public ItemIndictor(Item item, File cacheFile) {
		this.item = item;
		this.cacheFile = cacheFile;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public File getCacheFile() {
		return cacheFile;
	}

	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}
	
	public boolean addFuture(Future<?> e) {
		return futures.add(e);
	}

	public List<Future<?>> getFutures() {
		return futures;
	}

	public void setFutures(List<Future<?>> futures) {
		this.futures = futures;
	}

	public boolean isDownloading() {
		return downloading;
	}
	
	public void setDownloading() {
		 downloading = true;
	}
	
	public void setPause() {
		downloading = false;
	}
	
	public void pause() {
		setPause();
		for (Future<?> future : futures) {
			if(future.isDone()) continue;
			future.cancel(true);
		}
	}
	
	/**
	 * check and clear done and cancelled Future
	 */
	private void check() {
		Iterator<Future<?>> iterator =  futures.iterator();
		while (iterator.hasNext()) {
			Future<?> future = (Future<?>) iterator.next();
			if(future.isDone() || future.isCancelled()) {
				iterator.remove();
			}
		}
		
		
	}
	
	
	public void replaceDoneFuturesFromMax(DownloadPlane plane, SpeedMonitor... monitors) {
		check();
		RangeInfo info = item.getRangeInfo();
		for (int index = 0; index < info.getRangeCount(); index++) {
			if (info.isFinish(index)) {
				// System.out.println( "#"+ i + " is finish " );
				boolean updated = info.updateIndexFromMaxRange(index);
				if (updated) {
					Log.info(getClass(), "Item Update", item.toString());
					downloadPart(plane, index, monitors);
				}

			}
		}
	}
	
	public void download(DownloadPlane plane, SpeedMonitor... monitors) {
		futures = plane.download(item, monitors);
		setDownloading();
	}
	
	public void downloadPart(DownloadPlane plane, int index, SpeedMonitor... monitors) {
		Future<?>  future= plane.downloadPart(item, index, monitors);
		futures.add(future);
		setDownloading();
	}
	
	
	public void saveItem2CacheFile() {
		Utils.toJsonFile(cacheFile, item);
	}
	
	
	
	
}
