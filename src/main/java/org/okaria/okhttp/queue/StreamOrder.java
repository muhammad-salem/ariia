package org.okaria.okhttp.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface StreamOrder  {
	
	default List<Integer> streamDownloadOrder(int count){
		if(count == 0) return Collections.emptyList();
		List<Integer> indexs = new ArrayList<>();
		if(count == 1) {
			indexs.add(0);
		}
		else if(count > 1) {
			indexs.add(0);
			indexs.add(count-1);
			for (int i = 1; i < count-1; i++) {
				indexs.add(i);
			}
		}
		return indexs;
	}
}
