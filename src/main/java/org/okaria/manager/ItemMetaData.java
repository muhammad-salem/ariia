package org.okaria.manager;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.okaria.monitors.OneRangeMonitor;
import org.okaria.okhttp.queue.DownloadPlane;
import org.okaria.range.RangeUtil;
import org.okaria.segment.Segment;
import org.okaria.segment.Segment.OfferSegment;
import org.okaria.setting.Properties;
import org.okaria.speed.SpeedMonitor;
import org.okaria.util.R;
import org.terminal.console.log.Log;

public abstract class ItemMetaData implements OfferSegment, Closeable {
	
	protected Item item;
	protected RangeUtil info;
	
	protected boolean downloading = false;
	protected OneRangeMonitor rangeMointor;
	
	protected RandomAccessFile raf;
	private   ConcurrentLinkedQueue<Segment> segments;
	
	public ItemMetaData(Item item) {
		this.item 			= item;
		this.info 			= item.rangeInfo;
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
			} else {
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
	
	public boolean isDownloading() {
		return downloading;
	}
	
	public void checkCompleted() {
		Iterator<Integer> iterator = downloadList.iterator();
		while (iterator.hasNext()) {
			Integer index = iterator.next();
			if ( info.isFinish(index)){
				iterator.remove();
			} else {
				iterator.remove();
				waitQueue.add(index);
			}
		}
	}
	
	public void pause() {
		downloading = false;
		checkCompleted();
	}
	
	
	private LinkedList<Integer> downloadList = new LinkedList<>();
	private Queue<Integer> waitQueue = new LinkedList<>();
	
	public void initWaitQueue() {
		int count = info.getRangeCount();
		if (count == 0) return;
		waitQueue.clear();
		if (count <= 10 || info.limitOfIndex(count-1) != info.getFileLength()) {
			for (int index = 0; index < count; index++) {
				if (! info.isFinish(index)) {
					waitQueue.add(index);
				}
			}
		} else {
			for (int index = count-1, i = 0; i < 4; i++) {
				if (! info.isFinish(index-i)) {
					waitQueue.add(index-i);
				}
			}
			for (int index = 0; index < count - 4; index++) {
				if (! info.isFinish(index)) {
					waitQueue.add(index);
				}
			}
		}
	}

	public void startDownloadQueue(DownloadPlane plane, SpeedMonitor... monitors) {
		downloading = true;
		while ( downloadList.size() < Properties.RANGE_POOL_NUM & ! waitQueue.isEmpty()) {
			Integer index = waitQueue.poll();
			if(index == null) break;
			else {
				plane.downloadPart(this, index, rangeMointor, monitors);
				downloadList.add(index);
			}
		}
	}
	

	
	
//	public void downloadPart(DownloadPlane plane, int index, SpeedMonitor... monitors) {
//		Future<?>  future = plane.downloadPart(this, index, rangeMointor, monitors);
//		futures.add(future);
//		downloading = true;
//	}
	
	
	public void saveItem2CacheFile() {
		Item.toJsonFile(item);
	}
	
	
	
	
}
