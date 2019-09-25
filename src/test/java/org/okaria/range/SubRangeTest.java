package org.okaria.range;

import java.util.Arrays;

import org.junit.Test;

public class SubRangeTest {

	@Test
	public void createRanges() {
		System.out.println(Arrays.deepToString(SubRange.createRanges(1000, 10, 10)));
	}

}
