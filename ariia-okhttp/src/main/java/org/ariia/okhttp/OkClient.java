package org.ariia.okhttp;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.Objects;

import org.ariia.core.api.request.ClientRequest;
import org.ariia.core.api.request.Response;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkClient implements ClientRequest {
	
	private OkHttpClient httpClient;
	
	public OkClient(Proxy proxy) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder
			.cookieJar(OkCookieJar.CookieJarMap)
			.proxy(proxy)
			.retryOnConnectionFailure(false);
		this.httpClient = builder.build();
	}
	
	public OkClient(OkHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public Response executeRequest(String method, String url, Map<String, String> headers) throws IOException {
		
		Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers)) {
        	builder.headers(Headers.of(headers));
       	}
        
        builder .method(method, null)
        		.url(url);
        
        Call call  = httpClient.newCall(builder.build());
        okhttp3.Response response = call.execute();
        
        Response.Builder responseBuilder = new Response.Builder();
        responseBuilder.code(response.code());
        responseBuilder.requestUrl(response.networkResponse().request().url().toString());
        responseBuilder.requestMethod(response.request().method());
        responseBuilder.protocol(response.protocol().toString());
        responseBuilder.responseMessage(response.message());
        responseBuilder.headers(response.headers().toMultimap());
        responseBuilder.setBodyBytes(response.body().byteStream());
		return responseBuilder.build();
	}

	@Override
	public java.net.Proxy proxy() {
		return httpClient.proxy();
	}

}
