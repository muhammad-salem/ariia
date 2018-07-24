package org.okaria.range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.okaria.Utils;
import org.okaria.setting.AriaProperties;

public interface RangeUtils {

	default String getFileLengthMB(long[][] ranges) {
		return Utils.fileLengthUnite(getFileLength(ranges));
	}

	default long getFileLength(long[][] ranges) {
		long max = 0;

		for (long[] ls : ranges) {
			if (ls[1] > max)
				max = ls[1];
		}
		if (ranges[0][0] != 0)
			max += 2 * 1024 * 1024;
		return max;
	}

	default String getDownLengthMB(long[][] ranges) {
		return Utils.fileLengthUnite(getDownLength(ranges));
	}

	default long getDownLength(long[][] ranges) {

		return getFileLength(ranges) - getNotDownLength(ranges);
	}

	default String getDownLengthMB(long[][] ranges, long fileLength) {
		return Utils.fileLengthUnite(getDownLength(ranges, fileLength));
	}

	default long getDownLength(long[][] ranges, long start, long end) {

		return end - start - getNotDownLength(ranges);
	}

	default long getDownLength(long[][] ranges, long fileLength) {

		return fileLength - getNotDownLength(ranges);
	}

	default String getRengeLengthMB(long[][] ranges) {
		return Utils.fileLengthUnite(getRengeLength(ranges));
	}

	default long getNotDownLength(long[][] ranges) {
		long down = 0;
		long ends = 0;
		for (long[] ls : ranges) {
			down += ls[0];
			ends += ls[1];
		}
		return ends - down + ranges.length - 1; // 9kb download at empty state or ....
	}

	default long getRengeLength(long[][] ranges) {
		long length = 0;
		for (long[] ls : ranges) {
			long sub = ls[1] - ls[0];
			;
			if (sub > 0)
				length += sub;
		}
		return length;
	}

	default void printRange(long[][] ranges) {
		for (int i = 0; i < ranges.length; i++) {
			long[] ls = ranges[i];
			System.out.println("#" + i + "\t" + Arrays.toString(ls));
		}
	}

	default void printRange(List<long[][]> ranges) {
		for (int i = 0; i < ranges.get(0).length; i++) {
			System.out.print("#" + i);
			for (int j = 0; j < ranges.size(); j++) {
				long[] ls = ranges.get(j)[i];
				System.out.print("\t" + Arrays.toString(ls));
			}
			System.out.println();
		}
	}

	/** 
	*	create new range 
	*/
	default long[][] checkRanges(long[][] ranges) {
		
		if(ranges.length == 1 ){ return ranges;}
		long[][] newRange = null;
		if(ranges.length < AriaProperties.RANGE_POOL_NUM){
			newRange = new long[AriaProperties.RANGE_POOL_NUM][2];
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
/*	
	default long[][] avoidMissedBytes(long[][] ranges) {
		if(ranges[0][0] == 0 ) return ranges;
		for(int i = 0; i< ranges.length; i++){
			ranges[i] = avoidMissedBytes(ranges[i]);
		}
		return ranges;
	}
*/	
	default long[] avoidMissedBytes(long[] range) {
		// case of download complete in this range
		if( isSubRangeComplete(range) )
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
	 * @param ranges
	 * @return
	 */
	default boolean isSubRangeComplete(long[] ranges) {
		return ranges[0] - ranges[1] >= 0;
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

	default List<long[][]> checkRanges(List<long[][]> ranges) {
		ArrayList<long[][]> list = new ArrayList<long[][]>();
		for (long[][] ls : ranges) {
			list.add(checkRanges(ls));
		}
		return list;
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
	
//	default boolean isFinish(long[][] mat) {
//		if (mat == null)
//			throw new NullPointerException("Range Array has null value");
//		if(mat[0][1] == -1) return false;
//		long todownload = 0;
//		for (int i = 0; i < mat.length; i++) {
//			long[] ls = mat[i];
//			todownload += ls[1] - ls[0];
//		}
//		if (todownload <= 0)
//			return true;
//		return false;
//	}

	default boolean isFinish(List<long[][]> mat) {
		boolean result = false;
		for (long[][] ls : mat) {
			result &= isFinish(ls);
			if( ! result ) return false;
		}
		return true;
	}

	default String getRengeLengthMB(List<long[][]> ranges) {
		return Utils.fileLengthUnite(getRengeLength(ranges));
	}

	default long getRengeLength(List<long[][]> ranges) {
		long length = 0;
		for (long[][] ls : ranges) {
			length += getRengeLength(ls);
		}
		return length;
	}

	default String getDownLengthMB(List<long[][]> ranges, long length) {
		long down = 0;
		long partsize = (length / ranges.size());
		for (int i = 0; i < ranges.size(); i++) {
			down += getDownLength(ranges.get(i), i * partsize, (i + 1) * partsize);
		}
		return Utils.fileLengthUnite(down);
	}
	
	default long subLength(long[] range) {
		return range[1] - range[0];
	}
	default int indexOfMaxRange(long[][] ranges) {
		int index = -1;
		long max = 0;
		long sub = 0;
		for(int i = 0 ; i < ranges.length; i++){
			sub = subLength(ranges[i]);
			if(sub > max){
				max = sub;
				index = i;
			}
		}
		return index;
	}

}
