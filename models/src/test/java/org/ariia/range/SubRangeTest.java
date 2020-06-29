package org.ariia.range;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SubRangeTest {

    @Test
    public void createRanges() {
        System.out.println(Arrays.deepToString(SubRange.createRanges(1000, 10, 10)));
    }

}
