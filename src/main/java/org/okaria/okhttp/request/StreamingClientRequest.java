package org.okaria.okhttp.request;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;

public interface StreamingClientRequest extends ClientRequest {
	
    default Call getCall(HttpUrl url, long startRange, long endRange, List<Cookie> jar, Headers headers) throws IOException {
    	return getCall(url, startRange, jar, headers);
    }
    
}
