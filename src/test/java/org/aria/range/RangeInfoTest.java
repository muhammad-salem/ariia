package org.aria.range;

import org.ariia.range.RangeInfo;
import org.junit.Test;

public class RangeInfoTest {

	@Test
	public void test1() {
		RangeInfo info = RangeInfo.RangeOf64M(6629201711l);
		System.out.println(info);
	}
	@Test
	public void test2() {
		RangeInfo info =  RangeInfo.RangeOf512K(10000000l); //new RangeInfo(100000090000l);
		System.out.println(info);
	}

}
