package org.okaria.range;

import java.util.Arrays;

import org.okaria.setting.Properties;

public class RangeInfo implements RangeUtil {
	

    public static RangeInfo RangeInfo512K( long filelength) {
    	return RangeInfoByte(filelength, 524288);
    }
    public static RangeInfo RangeInfo1M( long filelength) {
    	return RangeInfoByte(filelength, 1048576);
    }
    public static RangeInfo RangeInfo2M( long filelength) {
    	return RangeInfoByte(filelength, 2097152);
    }
    public static RangeInfo RangeInfo3M( long filelength) {
    	return RangeInfoByte(filelength, 3145728);
    }
    public static RangeInfo RangeInfo4M( long filelength) {
    	return RangeInfoByte(filelength, 4194304);
    }
    public static RangeInfo RangeInfoByte( long filelength, final int chunkLength) {
    	int count = (int)(filelength / chunkLength) + ((int)(filelength%chunkLength) > 0 ? 1:0);
		long[][] range = new long[count][2];
		for (int index = 0; index < range.length; ) {
			range[index][0] = chunkLength *   index;
			range[index][1] = chunkLength * ++index;
		}
		range[count-1][1] = filelength;
		RangeInfo info = new RangeInfo(filelength, range);
    	return info;
    }
	
	protected long	fileLength;
	protected long	downloadLength;
	protected long  remainingLength;
	protected long[][]	range;
	
	public RangeInfo() {
		this.fileLength = -1l;
	    this.range = SubRange.mksubrange(fileLength);
    }

    /**
     * [length = -1] -> [streaming], [unknown length]
     */
    public RangeInfo(long length) {
    	this(length, Properties.RANGE_POOL_NUM);
    }
    
    
    public RangeInfo(long length, int rangeCount) {
        fileLength = length;
        initRange(rangeCount);
    }

    public RangeInfo(long length, long[][] range) {
        fileLength = length;
        this.range = range;
    }
	protected void initRange(int rangeCount) {
		if (range == null) {
            // create new range for that file
			
			if (fileLength >= 10485760)		// for 10MB
                range = SubRange.stream(fileLength, rangeCount);
                
			else if (fileLength >= 1048576)		// for 1MB
                range = SubRange.subrange(fileLength, rangeCount);
            else if (fileLength > 0)
                range = SubRange.mksubrange(fileLength);
            // if filelength = -1 -- mean it should to stream link
            else {
            	//fileLength = Long.MAX_VALUE;
            	range =  SubRange.mksubrange( fileLength /*Long.MAX_VALUE*/ );
            }
        }
	}
	
	@Override
	public long[][] getRange() { return range;}
	

	@Override
	public void updateFileLength(long length) { this.fileLength = length;}
	
	@Override
	public void updateRange(long[][] copy) {this.range = copy;}
	@Override
	public long getFileLength() {return fileLength;}
	@Override
	public long getDownloadLength() {return downloadLength;}
	@Override
	public long  getRemainingLength() {return remainingLength;}
	private void setDownloadLength(long download) {this.downloadLength = download;}
	private void setRemainingLength(long remaining) {this.remainingLength = remaining;}

	@Override
	public void oneCycleDataUpdate() {
		setRemainingLength(calculateRemainingLength());
		setDownloadLength(calculateDownloadLength());
	}
	
	private long calculateRemainingLength() {
		long length = 0;
		long len = 0;
		for (int index = 0; index < range.length; index++) {
			len = remainLengthOf(index);
			if( len > 0)
				length += len;
		}
		return length;
	}
	
	private long calculateDownloadLength() {
		return fileLength - remainingLength;
	}
	
	public boolean isStreaming() {
        return range.length == 1 & range[0][1] == -1;
    }
	
	public long getIJ(int i, int j) {
		return range[i][j];
	}
	
	public long startOfIndex(int index) {
		return range[index][0];
	}
	
	public long limitOfIndex(int index) {
		return range[index][1];
	}
	
	public void setIJ(int i, int j, long value) {
		range[i][j] = value;
	}
	
	public void addIJ(int i, int j, long value) {
		range[i][j] += value;
	}
	

	
	public void addStartOfIndex(int i, long value) {
		range[i][0] += value;
	}
	
	public void addEndOfIndex(int i, long value) {
		range[i][1] += value;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for( ; i < getRangeCount()-1; i++){
			builder.append(Arrays.toString(indexOf(i)));
			builder.append(", ");
			if(i%4 == 3) builder.append('\n');
		}
		builder.append(Arrays.toString(indexOf(i)));
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		RangeInfo info = (RangeInfo) obj;
		return this.fileLength == info.fileLength && this.range == info.range;
	}

	@Override
	public synchronized int updateIndexFromMaxRange(int index) {
		return RangeUtil.super.updateIndexFromMaxRange(index);
	}


}
