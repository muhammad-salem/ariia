package org.ariia.core.api.response;

import org.ariia.range.RangeResponseHeader;
import org.ariia.range.RangeUtil;

public interface ContentLength {


    default long extracteLength(String contentLengthHeader) {
        try {
            return Long.parseLong(contentLengthHeader);
        } catch (NumberFormatException e) {
            RangeResponseHeader header = new RangeResponseHeader(contentLengthHeader);
            return header.length;
        }
    }

    default void updateLength(RangeUtil range, String contentLengthHeader) {
        setLength(range, extracteLength(contentLengthHeader));
    }

    default void setLength(RangeUtil range, long length) {
        if (range.isStreaming()) {
            range.indexOf(0)[1] = length;
            range.updateFileLength(length);
        } else {
            range.updateFileLength(length);
        }
    }

}