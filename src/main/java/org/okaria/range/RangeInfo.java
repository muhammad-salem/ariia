package org.okaria.range;

import org.okaria.Utils;

public class RangeInfo {

	public static final int RANGE_POOL_NUM = 8;
    protected long		fileLength;
    protected long[][]	range = null;

    private static RangeUtils rangeUtils = new RangeUtils() {};

   
    public RangeInfo() {
       this(-1, RANGE_POOL_NUM);
    }

    /**
     * [length = -1] -> [streaming], [unknown length]
     */
    public RangeInfo(long length) {
    	this(length, RANGE_POOL_NUM);
    }
    public RangeInfo(long length, int numOfRange) {
    	
        fileLength = length;
        createSubRange(numOfRange);
    }

    public RangeInfo(long length, long[][] range) {
        fileLength = length;
        this.range = range;
    }

	protected void createSubRange(int numOfRange) {
		if (range == null) {
            // create new range for that file
            if (fileLength >= 10485760)
                range = SubRange.stream(fileLength, numOfRange);
            else if (fileLength >= 1048576)
                range = SubRange.subrange(fileLength, (int)(fileLength/1000000) );
            else if (fileLength > 0)
                range = SubRange.mksubrange(fileLength);
            // if filelength = -1 -- mean it have to be streamd 
            else {
            	//fileLength = Long.MAX_VALUE;
            	range =  SubRange.mksubrange( -1 /*Long.MAX_VALUE*/ );
            }
        }
	}

    public long[][] getRange() {
        return range;
    }

    public int getRangeCount() {
        return range.length;
    }

    public long[] getIndex(int index) {
    	if(index >= range.length) 
    		return null;
        return range[index];
    }
    

    public void setRange(long[][] range) {
        this.range = range;
    }

    public long getFileLength() {
        return fileLength;
    }
    
    public boolean isUnknowLength() {
        return fileLength == -1;
    }

    /**
     * the new length will used to init the range array
     * @param fileLength
     */
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isFinish() {
        return rangeUtils.isFinish(range);
    }
	
	/**
	* suppose not to call in downloading process
	*
	*/
	public void updateRangesCountLength(int newCountLength)	{
		int count = range.length;
		if(newCountLength > count){
			long[][] temp = new long[newCountLength][2];
			rangeUtils.updateValue(temp, range);
			range = rangeUtils.checkRanges(temp);
		}else if(newCountLength == count){
			// do nothing
			// 
		}else if(newCountLength < count){
			// do nothing
			// may be to connect the closed one if the difference is too low
		}
	}
	
	public boolean updateIndexFromMaxRange(int index)	{
		int maxindex = indexOfMaxRange();
//		System.out.println("max index = " + maxindex);
		if(maxindex == -1) return false;
		long[][] ls = SubRange.subrange(getIndex(maxindex), 2);
		
		// edit range 
		range[maxindex][0] = ls[0][0];
		range[maxindex][1] = ls[0][1];
		
		range[index][0] = ls[1][0];
		range[index][1] = ls[1][1];
		return true;
	}
	
	/**
	 * unstable operation 
	 * need to check boundre
	 * -------------
	 * 				-------------
	 * >>>>>>>>>>>>>>>>>
	 * >>>>>>>>>>>>>>>>>---------
	 * to remove
	 * -------------
	 * 				----
	 * @param info
	 */
	public void concateRange(RangeInfo info)	{
		int newLength = this.range.length + info.range.length;
		long[][] nwRange = new long[newLength][2];
		int i = 0;
		for ( ; i < this.range.length; i++) {
			nwRange[i] = this.range[i];
		}
		for ( int y = 0 ; y < info.range.length; y++, i++) {
			nwRange[i] = info.range[y];
		}
		this.range = nwRange;
	}
	
	

    //---------------- deleget operation -----------------//


    public String getFileLengthMB() {
        return Utils.fileLengthUnite(fileLength) ;//rangeUtils.getFileLengthMB(range);
    }

    public String getDownLengthMB() {
        return rangeUtils.getDownLengthMB(range, fileLength);
    }

    public long getDownLength() {
        return rangeUtils.getDownLength(range, fileLength);
    }

    public String getRengeLengthMB() {
        return rangeUtils.getRengeLengthMB(range);
    }

    public long getNotDownLength() {
        return rangeUtils.getNotDownLength(range);
    }

    public long getRengesLength() {
        return rangeUtils.getRengeLength(range);
    }


    public void printRange() {
        rangeUtils.printRange(range);
    }

    public boolean isFinish(int index) {
        return  rangeUtils.isFinish(range[index]);
    }
    
    public void checkRanges() {
        range = rangeUtils.checkRanges(range);
    }

    public void avoidMissedBytes() {
        range = rangeUtils.avoidMissedBytes(range);
    }

    public void avoidMissedBytes(int rangeIndex) {
        range[rangeIndex] = rangeUtils.avoidMissedBytes(range[rangeIndex]);
    }

    public boolean isSubRangeComplete(int index) {
        return rangeUtils.isSubRangeComplete(range[index]);
    }

    public void updateValue(long[][] copy) {
        rangeUtils.updateValue(range, copy);
    }
    
    public int indexOfMaxRange() {
        return rangeUtils.indexOfMaxRange(range);
    }
    
    public long getIndexLength(int index) {
        return rangeUtils.subLength(range[index]);
    }

}
