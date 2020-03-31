package org.aria.okhttp.client;

import org.aria.range.RangeResponseHeader;
import org.aria.range.RangeUtil;
import org.aria.setting.Properties;

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
			range.updateRange(range.split(range.indexOf(0), Properties.RANGE_POOL_NUM));
		}else {
			range.updateFileLength(length);
		}
	}

}