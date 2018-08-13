package org.okaria.manager;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.mointors.OneRangeMointor;
import org.okaria.okhttp.queue.DownloadPlane;
import org.okaria.range.RangeUtil;
import org.okaria.segment.Segment;
import org.okaria.segment.Segment.OfferSegment;
import org.okaria.speed.SpeedMonitor;

public abstract class ItemMetaData implements OfferSegment, Closeable {
	
	protected Item item;
	protected RangeUtil info;
	protected List<Future<?>> futures;
	protected boolean downloading = false;
	protected OneRangeMointor rangeMointor;
	
	protected RandomAccessFile raf;

	private ConcurrentLinkedQueue<Segment> segments;
	private ConcurrentLinkedQueue<Segment> segmentBackup;
	
	public ItemMetaData(Item item) {
		this.item = item;
		this.info = item.rangeInfo;
		this.rangeMointor	= new OneRangeMointor(item);
		this.segments		= new ConcurrentLinkedQueue<>();
		this.segmentBackup	= new ConcurrentLinkedQueue<>();
		initRandomAccessFile();
	}
	/**
	 * @param item
	 */
	protected void initRandomAccessFile() {
		try {
			R.mkParentDir(item.path());
			raf = new RandomAccessFile(item.path(), "rw");
			raf.setLength(info.getFileLength());
		} catch (FileNotFoundException e) {
			Log.error(getClass(), e.getMessage(), e.toString());
		} catch (IOException e) {
			Log.error(getClass(), e.getMessage(), e.toString());
		}
		
	}
	
	@Override
	public void close() {
		try {
			raf.close();
		} catch (IOException e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
		}
	}
	@Override
	public long startOfIndex(int index) {
		return info.startOfIndex(index);
	}
	@Override
	public long limitOfIndex(int index) {
		return info.limitOfIndex(index);
	}
	
	@Override
	public void offerSegment(Segment segment) {
		segments.add(segment);
		info.addStartOfIndex(segment.index, segment.buffer.limit());
	}
	
	public void systemFlush() {
		if(segments.isEmpty()) return;
		
		synchronized (segments) {
//			ConcurrentLinkedQueue<Segment> segmentQueue;
//			segmentQueue = segments;
//			segments = segmentBackup;
//			segmentBackup = segmentQueue;
			while (!segments.isEmpty()) {
				try {
					segmentBackup.add(segments.remove());
				} catch (Exception e) {
					break;
				}
			}
		}
		flush(segmentBackup);
	}
	protected abstract void flush(ConcurrentLinkedQueue<Segment> segmentQueue);
	
	public int segmentSize() {
		return segments.size();
	}
	

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public RangeUtil getRangeInfo() {
		return info;
	}
	
	public OneRangeMointor getRangeMointor() {
		return rangeMointor;
	}
	
	
	public boolean addFuture(Future<?> e) {
		return futures.add(e);
	}

	public List<Future<?>> getFutures() {
		return futures;
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
	
	
	public void checkDoneFuturesFromMax(DownloadPlane plane, SpeedMonitor... monitors) {
		check();
		for (int index = 0; index < info.getRangeCount(); index++) {
			if (info.isFinish(index)) {
				int maxIndex = info.updateIndexFromMaxRange(index);
				if (maxIndex > -1 & maxIndex < info.getRangeCount()) {
					Log.trace(getClass(), "Item Update", item.toString());
					downloadPart(plane, index, monitors);
				}
			}
		}
	}
	
	public void download(DownloadPlane plane, SpeedMonitor... monitors) {
		R.mkParentDir(item.path());
		futures = plane.download(this, rangeMointor, monitors);
		setDownloading();
	}
	
	public void downloadPart(DownloadPlane plane, int index, SpeedMonitor... monitors) {
		Future<?>  future= plane.downloadPart(this, index, rangeMointor, monitors);
		futures.add(future);
		setDownloading();
	}
	
	
	public void saveItem2CacheFile() {
		Item.toJsonFile(item);
	}
	
	
	
	
}
