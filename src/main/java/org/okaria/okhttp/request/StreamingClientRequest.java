package org.okaria.okhttp.request;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;

public interface StreamingClientRequest extends ClientRequest {
	
    default Call getCall(HttpUrl url, long startRange, long endRange, Headers headers) throws IOException {
    	return getCall(url, startRange, headers);
    }
    
}
