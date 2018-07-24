package org.okaria.range;

import java.util.Arrays;

import org.okaria.Utils;
import org.okaria.setting.AriaProperties;

public class RangeInfo {

	
    protected long		fileLength;
    protected long[][]	range = null;

    private static RangeUtils RangeUtils = new RangeUtils() {};

   
    public RangeInfo() {
       this(-1, AriaProperties.RANGE_POOL_NUM);
    }

    /**
     * [length = -1] -> [streaming], [unknown length]
     */
    public RangeInfo(long length) {
    	this(length, AriaProperties.RANGE_POOL_NUM);
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
			
			if (fileLength >= 10485760)		// for 10MB
                range = SubRange.stream(fileLength, numOfRange);
                
			else if (fileLength >= 1048576)		// for 1MB
                range = SubRange.subrange(fileLength, numOfRange);
            else if (fileLength > 0)
                range = SubRange.mksubrange(fileLength);
            // if filelength = -1 -- mean it should to stream link
            else {
            	//fileLength = Long.MAX_VALUE;
            	range =  SubRange.mksubrange( fileLength /*Long.MAX_VALUE*/ );
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

    public boolean isFullInfo() {
        return range!= null & fileLength > 0 & range[0][1] != -1;
    }
    
    public boolean isStreaming() {
        return range!= null & range.length == -1 & range[0][1] == -1;
    }
    
    public boolean isFinish() {
        return range!= null & range.length != -1 & RangeUtils.isFinish(range);
    }
	
	/**
	* suppose not to call in downloading process
	*
	*/
	public void updateRangesCountLength(int newCountLength)	{
		int count = range.length;
		if(newCountLength > count){
			long[][] temp = new long[newCountLength][2];
			RangeUtils.updateValue(temp, range);
			range = RangeUtils.checkRanges(temp);
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
		range[maxindex][0]	= ls[0][0];
		range[maxindex][1]	= ls[0][1];
		
		range[index][0]		= ls[1][0];
		range[index][1]		= ls[1][1];
		
		if(range[maxindex][1] > 1024) {
			range[maxindex][1]	+= 1024;
			range[index][0]		-= 1024;
		}
		
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for( ; i < getRangeCount()-1; i++){
			builder.append(Arrays.toString(getIndex(i)));
			builder.append(", ");
			if(i%4 == 3) builder.append('\n');
		}
		builder.append(Arrays.toString(getIndex(i)));
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		RangeInfo info = (RangeInfo) obj;
		return this.fileLength == info.fileLength && this.range == info.range;
	}
	
    //---------------- deleget operation -----------------//


    public String getFileLengthMB() {
        return Utils.fileLengthUnite(fileLength) ;//rangeUtils.getFileLengthMB(range);
    }

    public String getDownLengthMB() {
        return RangeUtils.getDownLengthMB(range, fileLength);
    }

    public long getDownLength() {
        return RangeUtils.getDownLength(range, fileLength);
    }

    public String getRengeLengthMB() {
        return RangeUtils.getRengeLengthMB(range);
    }

    public long getNotDownLength() {
        return RangeUtils.getNotDownLength(range);
    }

    public long getRengesLength() {
        return RangeUtils.getRengeLength(range);
    }


    public void printRange() {
        RangeUtils.printRange(range);
    }

    public boolean isFinish(int index) {
        return  RangeUtils.isFinish(range[index]);
    }
    
    public void checkRanges() {
        range = RangeUtils.checkRanges(range);
    }

    public void avoidMissedBytes() {
        range = RangeUtils.avoidMissedBytes(range);
    }

    public void avoidMissedBytes(int rangeIndex) {
        range[rangeIndex] = RangeUtils.avoidMissedBytes(range[rangeIndex]);
    }

    public boolean isSubRangeComplete(int index) {
        return RangeUtils.isSubRangeComplete(range[index]);
    }

    public void updateValue(long[][] copy) {
        RangeUtils.updateValue(range, copy);
    }
    
    public int indexOfMaxRange() {
        return RangeUtils.indexOfMaxRange(range);
    }
    
    public long getIndexLength(int index) {
        return RangeUtils.subLength(range[index]);
    }

}
