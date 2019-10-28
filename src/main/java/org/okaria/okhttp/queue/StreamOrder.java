package org.okaria.okhttp.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface StreamOrder  {
	
	default List<Integer> streamDownloadOrder(int count){
		if(count == 0) return Collections.emptyList();
		List<Integer> indexes = new ArrayList<>();
		if(count == 1) {
			indexes.add(0);
		}
		else if(count > 1) {
			indexes.add(0);
			indexes.add(count-1);
			for (int i = 1; i < count-1; i++) {
				indexes.add(i);
			}
		}
		return indexes;
	}
}
