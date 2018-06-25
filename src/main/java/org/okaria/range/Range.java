package org.okaria.range;

import java.util.List;
import java.util.Map;

public interface Range {

	/**
	 * the new length will used to initiate the range array
	 * 
	 * @param length
	 */
	void setLength(long length);
	long getLength();

	void setChunkLength(long chunk);
	long getChunkLength();

	void setNumOfRange(int numOfRange);
	int getRangeSize();
	
	void initRange();
	void initLoadedRange();
	
	long[][] getArrayRange();
	List<long[]> getListRange();
	Map<Long, Long> getMapRange();

	long[][] getCurrentDownloadArrayRange();
	List<long[]> getCurrentDownloadListRange();
	Map<Long, Long> getCurrentDownloadMapRange();
	
	
	long[] getIndex(int index);
	void updateIndex(int index, long[] newValue);
	boolean updateIndexFromMaxRange(int index);
	
	long[] getNextAvailableDownload();
	

	boolean isFinish();
	void checkFinish();

	

	/**
	 * suppose not to call in downloading process
	 * it happen only if the new length will be not 
	 * Long.MAX
	 */
	void updateRangesCountLength(int newCountLength);

	
}
