package org.ariia.core.api.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ThreadOrder {

    int getRangePoolNum();

    default List<Integer> threadDownloadOrder(int count) {
        if (count <= 0) return Collections.emptyList();
        var indexes = new ArrayList<Integer>();
        if (count == 1) {
            indexes.add(0);
        } else if (count < getRangePoolNum()) {
            indexes.add(0);
            indexes.add(count - 1);
            for (int i = 1; i < count - 1; i++) {
                indexes.add(i);
            }
        } else if (count >= getRangePoolNum()) {
            indexes.add(0);
            indexes.add(getRangePoolNum() - 1);
            for (int i = 1; i < getRangePoolNum() - 1; i++) {
                indexes.add(i);
            }
        }
        return indexes;
    }
}
