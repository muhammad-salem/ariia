package org.ariia.okhttp.client;

import org.ariia.range.RangeResponseHeader;
import org.ariia.range.RangeUtil;

import okhttp3.Response;

public interface ContentLength {

	
	default long extracteLength( Response response) {
		try {
			return Long.parseLong(response.header("Content-Length"));
		} catch (NumberFormatException e) {
			RangeResponseHeader header = new RangeResponseHeader(response.header("Content-Range"));
			return header.length;
		}
	}
	
	default void updateLength(RangeUtil range, Response response) {
		setLength(range, extracteLength(response));
	}
	
	default void setLength(RangeUtil range, long length) {
		if(range.isStreaming()) {
			range.indexOf(0)[1] = length;
			range.updateFileLength(length);
		}else {
			range.updateFileLength(length);
		}
	}

}