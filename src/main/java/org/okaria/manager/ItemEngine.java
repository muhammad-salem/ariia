package org.okaria.manager;

import java.util.LinkedList;
import java.util.concurrent.Future;

import org.okaria.range.RangeInfo;
import org.okaria.range.SubRange;
import org.okaria.speed.SpeedMonitor;

public class ItemEngine {
	Item item;
	RangeInfo rangeInfo;
	int numCurrentDownloadThreads = 0;
	
	LinkedList<SpeedMonitor> monitors;
	
	LinkedList<Future<?>> futures;
	LinkedList<Integer> nextIndex;
	
	public ItemEngine(Item item){
		this.item = item;
		this.rangeInfo = item.getRangeInfo();
		futures = new LinkedList<>();
		nextIndex = new LinkedList<>();
		monitors = new LinkedList<>();
	}
	
	/**
	* return the next range index avilable to download or -1 if all had been started
	*/
	public synchronized int offerRangeIndex(){
		if(numCurrentDownloadThreads < rangeInfo.getRangeCount()) {
			return numCurrentDownloadThreads++;
		}else if ( !nextIndex.isEmpty()){
			return nextIndex.remove(0);
		}
		return -1;
	}
	
	public boolean updateIndexHadFinished(int index){
		int maxIndex = rangeInfo.indexOfMaxRange();
		//if(rangeInfo.getIndexLength(maxIndex) <= 1024*1024/2 ){ return false;} 	// 0.5 Mbyte
		long[][] splitRanges = SubRange.subrange(rangeInfo.getIndex(maxIndex)[0], rangeInfo.getIndex(maxIndex)[1], 2);
		rangeInfo.getIndex(maxIndex)[0] = splitRanges[0][0];
		rangeInfo.getIndex(maxIndex)[1] = splitRanges[0][1];
		rangeInfo.getIndex(index)[0]	= splitRanges[1][0];
		rangeInfo.getIndex(index)[1]	= splitRanges[1][1];
		
		nextIndex.add(index);
		
		return true;
	}
	
}
