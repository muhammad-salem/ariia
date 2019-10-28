package org.okaria.manager;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.mointors.OneRangeMonitor;
import org.okaria.okhttp.queue.DownloadPlane;
import org.okaria.range.RangeUtil;
import org.okaria.segment.Segment;
import org.okaria.segment.Segment.OfferSegment;
import org.okaria.setting.Properties;
import org.okaria.speed.SpeedMonitor;

public abstract class ItemMetaData implements OfferSegment, Closeable {
	
	protected Item item;
	protected RangeUtil info;
	protected List<Future<?>> futures;
	
	protected boolean downloading = false;
	protected OneRangeMonitor rangeMointor;
	
	protected RandomAccessFile raf;
	private   ConcurrentLinkedQueue<Segment> segments;
	
	public ItemMetaData(Item item) {
		this.item = item;
		this.info = item.rangeInfo;
		this.rangeMointor	= new OneRangeMonitor(info, item.getFilename());
		this.segments		= new ConcurrentLinkedQueue<>();
		initRandomAccessFile();
		initMetaData();
	}
	
	protected abstract void    initMetaData();
	protected abstract boolean writeSegment(Segment segment) ;
	public    abstract void    forceUpdate();
//	public    abstract void    clearFile();
	
	
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
	
	public boolean isClose() {
		return !raf.getChannel().isOpen();
	}
	
	public boolean isOpen() {
		return raf.getChannel().isOpen();
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
	public void offerSegment(Segment segment) {
		if(segment.buffer.remaining() == 0 ) {
			releaseSegment(segment);
			return;
		}
		segments.add(segment);
		info.addStartOfIndex(segment.index, segment.buffer.limit());
	}
	
	public synchronized void systemFlush() {
		if(segments.isEmpty()) return;
		flush(segments);
		System.out.print(".");
	}
	
	private void  flush(Queue<Segment> queue) {
		StringBuilder report = new StringBuilder();
		Segment segment;
		while (!queue.isEmpty()) {
			segment = queue.peek();
			if(segment == null) break;
			if(writeSegment(segment)) {
				report.append(segment.toString());
				report.append('\n');
				queue.poll();
				releaseSegment(segment);
			}else {
				// check raf is opened
				if(raf == null || isClose() ) {
					initRandomAccessFile();
					initMetaData();
				}
			}
		}
		
		saveItem2CacheFile();
		forceUpdate();
		
		if(report.length() > 0)
			Log.trace(getClass(), "flush segments", report.toString());
	}
	
	
	public int segmentSize() {
		return segments.size();
	}

	@Override
	public long startOfIndex(int index) {
		return info.startOfIndex(index);
	}
	@Override
	public long limitOfIndex(int index) {
		return info.limitOfIndex(index);
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
	
	public OneRangeMonitor getRangeMointor() {
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
		check();
		for (Future<?> future : futures) {
			if(future.isDone() || future.isCancelled()) continue;
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
	
	
	public void recycelCompletedFromMax2(DownloadPlane plane, SpeedMonitor... monitors) {
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
	
	
	
	public void download2(DownloadPlane plane, SpeedMonitor... monitors) {
		R.mkParentDir(item.path());
		futures = plane.download(this, rangeMointor, monitors);
		setDownloading();
	}
	
	private LinkedList<Integer> download = new LinkedList<>();
	private Queue<Integer> waitting = new LinkedList<>();
	
	public void downloadThreads(DownloadPlane plane, SpeedMonitor... monitors) {
		
		int count = info.getRangeCount();
		if (count == 0) return;
		else if (count <= 10 || info.limitOfIndex(count-1) != info.getFileLength()) {
			for (int index = 0; index < count; index++) {
				if (! info.isFinish(index)) {
					waitting.add(index);
				}
			}
		}else {
			
			for (int index = count-1, i = 0; i < 4; i++) {
				if (! info.isFinish(index-i)) {
					waitting.add(index-i);
				}
			}
			
			for (int index = 0; index < count - 4; index++) {
				if (! info.isFinish(index)) {
					waitting.add(index);
				}
			}
			
			
		}
		
		
//		for (int index = 0; index < info.getRangeCount(); index++) {
//			if (! info.isFinish(index)) {
//				waitting.add(index);
//			}
//		}
		R.mkParentDir(item.path());
		futures = new LinkedList<Future<?>>();
		downloadAction(plane, monitors);
		setDownloading();
	}

	/**
	 * @param plane
	 * @param monitors
	 */
	protected void downloadAction(DownloadPlane plane, SpeedMonitor... monitors) {
		
		while ( download.size() <= Properties.RANGE_POOL_NUM & ! waitting.isEmpty()) {
			if ( ! downloadFirstInIndex(plane , monitors) ) break;
		}
		
		if(download.size() == Properties.RANGE_POOL_NUM) return;
		
//		if(download.size() < Properties.RANGE_POOL_NUM & waitting.isEmpty()) {
//			for ( int markIndex = 0; markIndex < info.getRangeCount(); markIndex++) {
//				if ( info.isFinish(markIndex)) {
//					int maxIndex = info.updateIndexFromMaxRange(markIndex);
//					if (maxIndex > -1 & maxIndex < info.getRangeCount()) {
//						waitting.add(markIndex);
//					}
//				}
//			}
//			return;
//		}
		
		//downloadAction(plane, monitors);
	}

	/**
	 * @param plane
	 * @param monitors
	 */
	protected boolean downloadFirstInIndex(DownloadPlane plane, SpeedMonitor... monitors) {
		Integer index = waitting.poll();
		if(index == null) return false;
		downloadPart(plane, index, monitors);
		download.add(index);
		return true;
	}
	
	public void checkCompleted(DownloadPlane plane, SpeedMonitor... monitors) {
		check();
		Iterator<Integer> iterator = download.iterator();
		while (iterator.hasNext()) {
			Integer index = (Integer) iterator.next();
			if ( info.isFinish(index) ){
				iterator.remove();
			}
		}
		downloadAction(plane, monitors);
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
