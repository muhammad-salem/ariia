package org.ariia.core.api.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ariia.config.Properties.RANGE_POOL_NUM;

public interface ThreadOrder {
	
	
	default List<Integer> threadDownloadOrder(int count){
		if(count <= 0) return Collections.emptyList();
		List<Integer> indexs = new ArrayList<>();
		if(count == 1) {
			indexs.add(0);
		}
		else if(count < RANGE_POOL_NUM) {
			indexs.add(0);
			indexs.add(count-1);
			for (int i = 1; i < count-1; i++) {
				indexs.add(i);
			}
		}
		else if(count >=  RANGE_POOL_NUM) {
			indexs.add(0);
			indexs.add(RANGE_POOL_NUM-1);
			for (int i = 1; i < RANGE_POOL_NUM-1; i++) {
				indexs.add(i);
			}
		}
		return indexs;
	}
}
