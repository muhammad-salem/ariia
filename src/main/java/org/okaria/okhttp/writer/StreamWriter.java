package org.okaria.okhttp.writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.log.concurrent.Log;
import org.okaria.R;
import org.okaria.manager.Item;
import org.okaria.manager.ItemMetaData;
import org.okaria.segment.Segment;

public class StreamWriter extends ItemMetaData {
	
	private FileChannel channel;
	public StreamWriter(Item item) {
		super(item);
	}
	
	
	@Override
	protected void initRandomAccessFile() {
		try {
			R.mkParentDir(item.path());
			raf = new RandomAccessFile(item.path(), "rw");
			raf.seek(info.getFileLength());
			channel = raf.getChannel();
		} catch (FileNotFoundException e) {
			Log.error(getClass(), e.getMessage(), e.toString());
		} catch (IOException e) {
			Log.error(getClass(), e.getMessage(), e.toString());
		}
	}


	// suppose one range rangInfo[1][2]

	@Override
	protected void flush(ConcurrentLinkedQueue<Segment> segmentQueue){
		Iterator<Segment> iterator =  segmentQueue.iterator();
		StringBuilder report = new StringBuilder();
		while (iterator.hasNext()) {
			Segment segment = (Segment) iterator.next();
			try {
				while (segment.buffer.hasRemaining()) {
					channel.write(segment.buffer);
				}
				report.append(segment.toString());
				report.append('\n');
			} catch (IOException e) {
				Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
				if(raf != null) close();
				initRandomAccessFile();
				if(raf == null) return; 
				continue;		// could lead to infinite loop 
			}
			
			releaseSegment(segment);
			iterator.remove();
		}
		if(report.length() > 0)
			Log.trace(getClass(), "flush segments", report.toString());
		//saveItem2CacheFile();
	}


}
