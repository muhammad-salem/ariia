package org.ariia.range;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SubRangeTest {

	@Test
	public void createRanges() {
		System.out.println(Arrays.deepToString(SubRange.createRanges(1000, 10, 10)));
	}

}
