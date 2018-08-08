package org.okaria.range;

import java.util.ArrayList;
import java.util.List;

import org.okaria.Utils;
import org.okaria.setting.Properties;

public interface Util {

	default String getDownLengthMB(long[][] ranges, long fileLength) {
		return Utils.fileLengthUnite(getDownLength(ranges, fileLength));
	}


	default long getRemainingLength(long[][] ranges) {
		long length = 0;
		for (long[] ls : ranges) {
			long sub = ls[1] - ls[0];
			if (sub > 0)
				length += sub;
		}
		return length;
	}
	default long getDownLength(long[][] ranges, long fileLength) {
		return fileLength - getRemainingLength(ranges);
	}

	default String getRemainingLengthMB(long[][] ranges) {
		return Utils.fileLengthUnite(getRemainingLength(ranges));
	}

	/** 
	*	create new range 
	*/
	default long[][] checkRanges(long[][] ranges) {
		
		if(ranges.length == 1 ){ return ranges;}
		long[][] newRange = null;
		if(ranges.length < Properties.RANGE_POOL_NUM){
			newRange = new long[Properties.RANGE_POOL_NUM][2];
		}
		else {
			newRange = new long[ranges.length][2];
		}
		
		
		int items = 0;
		for (long[] ls : ranges) {
			if (isFinish(ls))	// ls[0] - ls[1] >= 0
				continue;
			newRange[items++] = ls;
		}

		for (int i = 0; items < newRange.length; i++, items++) {
			long[][] split = SubRange.subrange(newRange[i][0], newRange[i][1], 2);
			
			if(split[0][1] > 1024){
				split[0][1] += 1024;
				split[1][0] -= 1024;
			}
			
			newRange[i] = split[0];
			newRange[items] = split[1];
		}
		return newRange;
	}
	
	
	default List<long[][]> avoidMissedBytes(List<long[][]> ranges) {
		ArrayList<long[][]> list = new ArrayList<long[][]>();
		for (long[][] ls : ranges) {
			list.add(avoidMissedBytes(ls));
		}
		return list;
	}
	
	default long[][] avoidMissedBytes(long[][] ranges) {
		if(ranges[0][0] > 0 ) ranges[0] = avoidMissedBytes(ranges[0]);
		for(int i = 1; i< ranges.length; i++){
			if(ranges[i][0] - ranges[i-1][1]  > 1)
				ranges[i] = avoidMissedBytes(ranges[i]);
		}
		return ranges;
	}
	
	default long[] avoidMissedBytes(long[] range) {
		// case of download complete in this range
		if( isComplete(range) )
			return range;
		if(range[0] >= 22000)		// 
			range[0] -= 20000;		// 144256	
		else 
		if(range[0] >= 12000)
			range[0] -= 10000;
			
		if(range[0] <= 0)
			range[0] = 0;
		return range;
	}
	
	/**
	 * call by reference to change the values of holder array to copy array 
	 * @param holder
	 * @param copy
	 */
	default void updateValue(long[][] holder, long[][] copy) {
		for (int i = 0; i < holder.length; i++) {
			for (int j = 0; j < holder[0].length; j++) {
				holder[i][j] = copy[i][j];
			}
		}
	}
	
	default boolean isComplete(long[] ranges) {
		return ranges[0] - ranges[1] >= 0;
	}

	default boolean isFinish(long[] mat) {
		if (mat == null)
			throw new NullPointerException("mat has null value");
		return mat[1] > -1 & (mat[0] - mat[1] >= 0);
	}
	
	default boolean isFinish(long[][] mat) {
		for (long[] ls : mat) {
			if( ! isFinish(ls) ) return false;		// at least one array not yet had finished
		}
		return true;
	}
	
	default long lengthOf(long[] range) {
		return range[1] - range[0];
	}
	
	default int indexOfMaxRange(long[][] ranges) {
		int index = -1;
		long max = 0;
		long length = 0;
		for(int i = 0 ; i < ranges.length; i++){
			length = lengthOf(ranges[i]);
			if(length > max){
				max = length;
				index = i;
			}
		}
		return index;
	}

}
