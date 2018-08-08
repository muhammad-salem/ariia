package org.okaria.range;

public interface Range {

	long[][] getRange();
	int getRangeCount();
	/**
	 * call by reference to change the values of holder array to copy array 
	 * @param holder
	 * @param copy
	 */
	void updateRange(long[][] copy);
	
	long getFileLength();
	long getDownloadLength();
	long getRemainingLength();
	
	
	String getFileLengthMB();
	String getDownloadLengthMB();
	String getRemainingLengthMB();
	
	void checkRanges();
	void avoidMissedBytes();
	void avoidMissedBytes(int index);
	
	
	boolean isFinish();
	boolean isFinish(int index);
	long[] indexOf(int index);
	long remainLengthOf(int index);
	
	int indexOfMaxRange();
	void updateIndex(int index, long[] newValue);
	int updateIndexFromMaxRange(int index);
	

	
	
		
	// cycle 
	void oneCycleDataUpdate();

	
}
