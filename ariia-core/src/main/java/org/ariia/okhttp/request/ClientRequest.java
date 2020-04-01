package org.ariia.okhttp.request;

import java.io.IOException;
import java.util.Objects;

import org.ariia.items.Item;
import org.ariia.logging.Log;
import org.ariia.range.RangeUtil;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface ClientRequest {
	
	OkHttpClient getHttpClient();
	
    default Headers getHeaders(Item item) {
    	return item.getHeaders().isEmpty() ? null : Headers.of(item.getHeaders());
    }
    
	default Response head(String url) throws IOException {
		return head(HttpUrl.parse(url),  null);
	}
	default Response head(HttpUrl url) throws IOException {
		return head(url, null);
	}
	default Response head(String url, Headers headers) throws IOException {
        return head(HttpUrl.parse(url), headers);
    }
    default Response head(Item item) throws IOException {
        return response( headCall(HttpUrl.parse(item.getUrl()), getHeaders(item)) );
    }

	default Response head(HttpUrl url, Headers headers) throws IOException {
        return response(headCall(url, headers));
    }
    default Call headCall(HttpUrl url, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder().head().url(url);
        if (Objects.nonNull(headers)) {
        	builder.headers(headers);
		}
        return newCall(builder.build());
    }
    
	//--------------------------------------------------//
	
	
	default Response get(String url) throws IOException {
		return get(HttpUrl.parse(url),  null);
	}
	
	default Response get(HttpUrl url) throws IOException {
		return get(url, null);
	}


	default Response get(HttpUrl url, long startRange, long endRange) throws IOException {
		return get(url, startRange, endRange, null);
	}


	default Response get(HttpUrl url, long startRange) throws IOException {
		return get(url, startRange, null);
	}
	//-----------------------------------------------------------------------------//

    
	default Response get(String url, Headers headers) throws IOException {
    	return response(getCall(HttpUrl.parse(url), headers));
    }
	
	default Response get(HttpUrl url, Headers headers) throws IOException {
    	return response(getCall(url, headers));
    }
    
    default Response get(Item item) throws IOException {
        return response(getCall(item, 0));
    }
    
    default Response get(Item item, int index) throws IOException {
        return response(getCall(item, index));
    }
    
    default Call getCall(Item item, int index) throws IOException {
    	RangeUtil info = item.getRangeInfo();
        return getCall(item, info.indexOf(index)[0], info.indexOf(index)[1]);
    }
    
    default Response get(Item item, long startRange, long endRange) throws IOException {
        return response(getCall(item, startRange, endRange));
    }
    default Call getCall(Item item, long startRange, long endRange) throws IOException {
        return getCall(HttpUrl.parse(item.getUrl()), 
        		startRange, 
        		endRange, 
        		getHeaders(item));
    }
    
    default Response get(HttpUrl url, long startRange, long endRange, Headers headers) throws IOException {
        if(endRange == -1) return get(url, startRange, headers);
    	return response(getCall(url, startRange, endRange, headers));
    }
    
   
    
    default Response get(HttpUrl url, long startRange, Headers headers) throws IOException {
    	return response(getCall(url, startRange, headers));
    }
    
    default Call getCall(HttpUrl url, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers)) {
        	builder.headers(headers);
       	}
        builder.get().url(url);
        return newCall(builder.build());
    }
    

    default Response getStream(HttpUrl url, Headers headers) throws IOException {
    	return response(getStreamCall(url, headers));
    }
    default Call getStreamCall(HttpUrl url, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers)) {
        	builder.headers(headers);
       	}
        builder.get()
                .url(url)
                .addHeader("Range", "bytes=0-");
        return newCall(builder.build());
    }
    
    default Call getCall(HttpUrl url, long startRange, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers)) {
        	builder.headers(headers);
       	}
        builder.get()
        		.url(url)
                .addHeader("Range", "bytes=" + startRange + "-");
        return newCall(builder.build());
    }
    
    
    default Call getCall(HttpUrl url, long startRange, long endRange, Headers headers) throws IOException {
    	if(endRange == -1) return getCall(url, startRange, headers);
    	Request.Builder builder = new Request.Builder();
        if (Objects.nonNull(headers)) {
        	builder.headers(headers);
       	}
        builder.get().url(url)
        		.addHeader("Range", "bytes=" + startRange + "-" + endRange);
        return newCall(builder.build());
    }
    
    default Call newCall( Request request) {
        return getHttpClient().newCall(request);
    }
    
    default Response response( Call call) throws IOException {
    	Response response = call.execute();
    	debugResponse(response);
        return response;
    }
    
    default void debugResponse( Response response) {
    	Request request = response.networkResponse().request();
    	StringBuilder builder = new StringBuilder();
    	builder.append("request send:");
    	builder.append('\n');
    	builder.append(request.method());
    	builder.append(' ');
    	builder.append(request.url());
    	builder.append(' ');
    	builder.append(response.protocol());
    	builder.append('\n');
    	builder.append(request.headers().toString());
    	builder.append('\n');
  
    	builder.append("response begin:");
    	builder.append('\n');
    	builder.append(response.protocol());
    	builder.append(' ');
    	builder.append(response.code());
    	builder.append(' ');
    	builder.append(response.message());
    	builder.append('\n');
    	builder.append(response.headers().toString());
    	Log.debug(getClass(), "Client Request / Server Response", builder.toString());
    }
}
