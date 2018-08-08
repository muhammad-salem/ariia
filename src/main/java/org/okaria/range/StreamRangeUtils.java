package org.okaria.range;

public interface StreamRangeUtils extends Util {

	default long[][] checkRanges(long[][] ranges) {
		return ranges;
	}
}
