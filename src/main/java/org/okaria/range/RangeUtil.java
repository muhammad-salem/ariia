package org.okaria.range;

import org.okaria.Utils;
import org.okaria.setting.Properties;

public interface RangeUtil extends Range {
	default int getRangeCount() {
		return getRange().length;
	}
	default String getFileLengthMB() {
		return Utils.fileLengthUnite(getFileLength());
	}
	default String getDownloadLengthMB() {
		return Utils.fileLengthUnite(getDownloadLength());
	}
	default String getRemainingLengthMB() {
		return Utils.fileLengthUnite(getRemainingLength());
	}
	default void checkRanges() {
		if (getRangeCount() == 1)
			return;
		long[][] newRange = null;
		if (getRangeCount() < Properties.RANGE_POOL_NUM) {
			newRange = new long[Properties.RANGE_POOL_NUM][2];
		} else {
			newRange = new long[getRangeCount()][2];
		}
		int items = 0;
		for (long[] ls : getRange()) {
			if (isFinish(ls)) // ls[0] - ls[1] >= 0
				continue;
			newRange[items++] = ls;
		}

		for (int i = 0; items < newRange.length; i++, items++) {
			long[][] split = SubRange.subrange(newRange[i], 2);

			if (split[0][1] > 1024) {
				split[0][1] += 1024;
				split[1][0] -= 1024;
			}

			newRange[i] = split[0];
			newRange[items] = split[1];
		}

		updateRange(newRange);
	}
	default void avoidMissedBytes() {
		for (int i = 0; i < getRangeCount(); i++) {
			if (remainLengthOf(i) > 1)
				avoidMissedBytes(i);
		}
	}
	default void avoidMissedBytes(int index) {
		// case of download complete in this range
		if (isFinish(index))
			return;
		if (indexOf(index)[0] >= 22000) //
			indexOf(index)[0] -= 20000; // 144256
		else if (indexOf(index)[0] >= 12000)
			indexOf(index)[0] -= 10000;

		if (indexOf(index)[0] <= 0)
			indexOf(index)[0] = 0;
	}

	default long[] indexOf(int index) {
		return getRange()[index];
	}
	/**
	 * 
	 */
	default long remainLengthOf(int index) {
		return getRange()[index][1] - getRange()[index][0];
	}

	default boolean isFinish(long[] ls) {
		return (ls[1] != -1) && (ls[0] - ls[1] >= 0);
	}
	default boolean isFinish(int index) {
		return isFinish(indexOf(index));
	}
	default boolean isFinish() {
		for (int index = 0; index < getRangeCount(); index++) {
			if (!isFinish(index))
				return false; // at least one array not yet had finished
		}
		return true;
	}

	default int indexOfMaxRange() {
		int index = -1;
		long max = 0;
		long length = 0;
		for (int i = 0; i < getRangeCount(); i++) {
			length = remainLengthOf(i);
			if (length > max) {
				max = length;
				index = i;
			}
		}
		return index;
	}
	default void updateIndex(int index, long[] newValue) {
		getRange()[index] = newValue;
	}
	
	default int updateIndexFromMaxRange(int index) {
		int maxIndex = indexOfMaxRange();
		if (maxIndex == -1 && maxIndex < getRangeCount())
			return maxIndex;
		if (getRemainingLength() < 262144)
			return -1;
		long[][] ls = SubRange.subrange(indexOf(maxIndex), 2, 1024);
		// edit range
		updateIndex(index, ls[0]);
		updateIndex(maxIndex, ls[1]);
		return maxIndex;
	}

	// /**
	// * suppose not to call in downloading process
	// * it happen only if the new length will be not
	// * Long.MAX
	// */
	// default void updateRangesCountLength(int newCountLength) {
	// // TODO Auto-generated method stub
	// // ignored for now
	// }

}
