package org.ariia.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RangeInfoTest {

	@Test
	public void test1() {
		RangeInfo info = RangeInfo.RangeOf64M(6629201711L);
		System.out.println(info);
	}
	@Test
	public void test2() {
		RangeInfo info =  RangeInfo.RangeOf512K(10000000L); //new RangeInfo(100000090000l);
		System.out.println(info);
	}
	
	@Test
	public void test3() {
		RangeInfo info =  
				RangeInfo.recommendedRange(2_034_707_292_160L);
//		System.out.println(info);
		assertEquals(2_034_707_292_160L, info.limitOfIndex(1));
	}
	
	@Test
	public void test4() {
		RangeInfo info =  
				RangeInfo.recommendedRange(null);
		System.out.println(info);
	}
	
	@Test
	public void test5() {
		RangeInfo info =  
				RangeInfo.recommendedRange((long)Integer.MAX_VALUE);
		System.out.println(info);
	}

}
