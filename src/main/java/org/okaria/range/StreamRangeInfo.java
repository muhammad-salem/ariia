package org.okaria.range;

public class StreamRangeInfo extends RangeInfo {

	//private static RangeUtils RangeUtils = new RangeUtils() {};
	 
	public StreamRangeInfo() {
		super(-1, 1);
	}

	@Override
	protected void initRange(int numOfRange) {
		if (range == null) {
            // create new range for that file
			range =  SubRange.mksubrange( fileLength /*Long.MAX_VALUE*/ );
        }
	}

}
