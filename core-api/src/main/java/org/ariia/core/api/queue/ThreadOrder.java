package org.ariia.core.api.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ThreadOrder {

    int getRangePoolNum();

    default List<Integer> threadDownloadOrder(int count) {
        if (count <= 0) return Collections.emptyList();
        List<Integer> indexs = new ArrayList<>();
        if (count == 1) {
            indexs.add(0);
        } else if (count < getRangePoolNum()) {
            indexs.add(0);
            indexs.add(count - 1);
            for (int i = 1; i < count - 1; i++) {
                indexs.add(i);
            }
        } else if (count >= getRangePoolNum()) {
            indexs.add(0);
            indexs.add(getRangePoolNum() - 1);
            for (int i = 1; i < getRangePoolNum() - 1; i++) {
                indexs.add(i);
            }
        }
        return indexs;
    }
}
