package org.ariia.okhttp.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ariia.setting.Properties;

public interface ThreadOrder {
	
	public static int GET_RANGE_POOL_NUM() {
		return Properties.RANGE_POOL_NUM;
	}
	
	default List<Integer> threadDownloadOrder(int count){
		if(count <= 0) return Collections.emptyList();
		List<Integer> indexs = new ArrayList<>();
		if(count == 1) {
			indexs.add(0);
		}
		else if(count < GET_RANGE_POOL_NUM()) {
			indexs.add(0);
			indexs.add(count-1);
			for (int i = 1; i < count-1; i++) {
				indexs.add(i);
			}
		}
		else if(count >=  GET_RANGE_POOL_NUM()) {
			indexs.add(0);
			indexs.add(GET_RANGE_POOL_NUM()-1);
			for (int i = 1; i < GET_RANGE_POOL_NUM()-1; i++) {
				indexs.add(i);
			}
		}
		return indexs;
	}
}
