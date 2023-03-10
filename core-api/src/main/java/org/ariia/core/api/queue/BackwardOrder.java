package org.ariia.core.api.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface BackwardOrder {

    default List<Integer> streamDownloadOrder(int count) {
        if (count == 0) return Collections.emptyList();
        var indexes = new ArrayList<Integer>();
        if (count == 1) {
            indexes.add(0);
        } else if (count > 1) {
            indexes.add(count - 1);
            indexes.add(0);
            for (int i = count - 1; i > 1; i--) {
                indexes.add(i);
            }
        }
        return indexes;
    }
}
