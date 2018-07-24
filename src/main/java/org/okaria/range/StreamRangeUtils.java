package org.okaria.range;

public interface StreamRangeUtils extends RangeUtils {

	default long[][] checkRanges(long[][] ranges) {
		return ranges;
	}
}
