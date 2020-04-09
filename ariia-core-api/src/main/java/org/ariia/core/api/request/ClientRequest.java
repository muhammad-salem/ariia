package org.ariia.core.api.request;

import java.io.IOException;
import java.net.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.ariia.items.Item;
import org.ariia.logging.Log;


public interface ClientRequest {
	
	default Response head(String url) throws IOException {
    	return executeAndDebugRequest("HEAD", url, Collections.emptyMap());
	}
    
	default Response head(Item item) throws IOException {
		return executeAndDebugRequest("HEAD", item.getUrl(), item.getHeaders());
	}
	
    default Response head(String url, Map<String, String> headers) throws IOException {
    	return executeAndDebugRequest("HEAD", url, headers);
	}
    
    default Response get(String url) throws IOException {
    	return executeAndDebugRequest("GET", url, Collections.emptyMap());
    }
    default Response get(String url, Map<String, String> headers) throws IOException {
		return executeAndDebugRequest("GET", url, headers);
	}
	
	default Response get(Item item) throws IOException {
		return executeAndDebugRequest("GET", item.getUrl(), item.getHeaders());
	}
    
	default Response get(Item item, int index)  throws IOException{
		long[] range = item.getRangeInfo().indexOf(index);
		return get(item.getUrl(), range[0], range[1], item.getHeaders());
	}
	
	default Response get(String url, Long startRange, Long endRange, Map<String, String> headers) throws IOException {

		Map<String, String> headersCopy = new HashMap<>(headers);
		if (Objects.nonNull(startRange) || startRange > -1) {
			if (Objects.isNull(endRange) || endRange == -1) {
				headersCopy.put("Range", "bytes=" + startRange + "-");
			} else {
				headersCopy.put("Range", "bytes=" + startRange + "-" + endRange);
			}
		}
		return executeAndDebugRequest("GET", url, headers);
		
	}
	
	default Response executeAndDebugRequest(String method, String url, Map<String, String> headers) throws IOException {
		Response response = executeRequest(method, url, headers);
		debugResponse(response);
		return response;
	}
	
	default void debugResponse( Response response) {
//    	Request request = response.networkResponse().request();
    	StringBuilder builder = new StringBuilder();
    	builder.append("request send:\n");
    	builder.append('\n');
    	builder.append(response.requestMethod());
    	builder.append(' ');
    	builder.append(response.requestUrl());
    	builder.append(' ');
    	builder.append(response.protocol());
    	builder.append('\n');
  
    	builder.append("response begin:\n");
    	builder.append('\n');
    	builder.append(response.protocol());
    	builder.append(' ');
    	builder.append(response.code());
    	builder.append(' ');
    	builder.append(response.responseMessage());
    	builder.append('\n');
    	response.headers().forEach((headerName, vauleList) -> {
    		vauleList.forEach(value -> {
    			builder.append(headerName);
    			builder.append(": ");
    			builder.append(value);
    			builder.append('\n');
    		});
    	});
//    	builder.append(response.headers().toString());
    	Log.debug(getClass(), "Client Request / Server Response", builder.toString());
    }

	
	Response executeRequest(String method, String url, Map<String, String> headers) throws IOException;
	Proxy proxy();
}
