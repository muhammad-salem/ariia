package org.okaria.range;

import org.junit.Test;

public class RangeInfoTest {

	@Test
	public void test() {
//		RangeInfo info =  RangeInfo.RangeInfo512K(10000000l); //new RangeInfo(100000090000l);
////		System.out.println(Arrays.deepToString(info.range));
//		System.out.println(info);
//		
//		info.range = RangeInfo.StreamOrder(info.range);
////		System.out.println(Arrays.deepToString(info.range));
//		System.out.println(info);

		RangeInfo info = RangeInfo.RangeInfo2M(6629201711l);
		System.out.println(info);
	}

}
