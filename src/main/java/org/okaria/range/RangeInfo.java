package org.okaria.range;

import java.util.Arrays;


public class RangeInfo implements RangeUtil {
	

    public static RangeInfo RangeOf512K( long fileLength) {
    	return RangeOfByte(fileLength, 524288/*1024×512*/);
    }
    public static RangeInfo RangeOf1M( long fileLength) {
    	return RangeOfByte(fileLength, 1048576/*1024×1024×1*/);
    }
    public static RangeInfo RangeOf2M( long fileLength) {
    	return RangeOfByte(fileLength, 2097152/*1024×1024×2*/);
    }
    public static RangeInfo RangeOf4M( long fileLength) {
    	return RangeOfByte(fileLength, 4194304/*1024×1024×4*/);
    }
    public static RangeInfo RangeOf8M( long fileLength) {
    	return RangeOfByte(fileLength, 8388608 /*1024×1024×8*/);
    }
    public static RangeInfo RangeOf16M( long fileLength) {
    	return RangeOfByte(fileLength, 16777216/*1024×1024×16*/);
    }
    public static RangeInfo RangeOf32M( long fileLength) {
    	return RangeOfByte(fileLength, 33554432/*1024×1024×32*/);
    }
    public static RangeInfo RangeOf64M( long fileLength) {
    	return RangeOfByte(fileLength, 67108864/*1024×1024×64*/);
    }
    
    
    public static RangeInfo RangeOfByte( long fileLength, final int chunkLength) {
		long[][] rangeArray = rangeArray(fileLength, chunkLength);
    	return new RangeInfo(fileLength, rangeArray);
    }
    
	private static long[][] rangeArray(final long fileLength, final int chunkLength) {
    	int count = (int)(fileLength / chunkLength) + ((int)(fileLength%chunkLength) > 0 ? 1:0);
		long[][] rangeArray = new long[count][2];
		final long longChunk = chunkLength;
		for (int index = 0; index < rangeArray.length; ) {
			rangeArray[index][0] =   index * longChunk;
			rangeArray[index][1] = ++index * longChunk;
		}
		rangeArray[count-1][1] = fileLength;
		return rangeArray;
	}
	
	

	protected long		fileLength;
	protected long		downloadLength;
	protected long  	remainingLength;
	protected long[][]	range;

	/**
	 * stream contractor, unknown length
	 */
	public RangeInfo() {
		this.fileLength = -1l;
	    this.range = new long[][] {{0, -1}};
    }

    /**
     * default range for stream with knowing length
     *  
     * [length = -1] -> [streaming], [unknown length]
     */
    public RangeInfo(long length) {
    	this(length, 1);
    }
    
    
    public RangeInfo(long length, int count) {
        fileLength = length;
        this.range = split(0, length, count);
    }

    public RangeInfo(long length, long[][] range) {
    	this.fileLength = length;
        this.range = range;
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
	
	@Override
	public boolean isStreaming() {
        return range.length == 1 & range[0][0] >= 0;
    }
	
	@Override
	public boolean hadLength() {
        return range.length == 1 & range[0][1] == -1;
    }
	
	public long getIJ(int i, int j) {
		return range[i][j];
	}
	
	@Override
	public long startOfIndex(int index) {
		return range[index][0];
	}
	
	@Override
	public long limitOfIndex(int index) {
		return range[index][1];
	}
	
	public void setIJ(int i, int j, long value) {
		range[i][j] = value;
	}
	
	public void addIJ(int i, int j, long value) {
		range[i][j] += value;
	}
	

	
	@Override
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
