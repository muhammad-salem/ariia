package org.okaria.okhttp.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.log.concurrent.Log;
import org.okaria.manager.Item;
import org.okaria.range.RangeInfo;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface ClientRequest {
	
	OkHttpClient getHttpClient();
	
	default Response head(String url) throws IOException {
		return head(HttpUrl.parse(url), new ArrayList<>(), Headers.of(new HashMap<>()));
	}

	default Response head(HttpUrl url) throws IOException {
		return head(url, new ArrayList<>(), Headers.of(new HashMap<>()));
	}

	default Response head(HttpUrl url, List<Cookie> jar) throws IOException {
		return head(url, jar, Headers.of(new HashMap<>()));
	}

	
	default Response head(String url, Headers headers) throws IOException {
        return head(HttpUrl.parse(url), new ArrayList<>(), headers);
    }

    default Response head(HttpUrl url, Headers headers) throws IOException {
        return head(url, new ArrayList<>(), headers);
    }

    default Response head(Item item) throws IOException {
        return response( headCall(item.getUpdateUrl(), item.getCookies(), item.getHeaders()) );
    }
    
    default Response head(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
        return response(headCall(url, jar, headers));
    }
    default Call headCall(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
        getHttpClient().cookieJar().saveFromResponse(url, jar);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .headers(headers)
                .head();
        return newCall(builder.build());
    }
    
	//--------------------------------------------------//
	
	
	default Response get(HttpUrl url) throws IOException {
		return get(url, new ArrayList<>(), Headers.of(new HashMap<>()));
	}

	default Response get(HttpUrl url, List<Cookie> jar) throws IOException {
		return get(url, jar, Headers.of(new HashMap<>()));
	}

	default Response get(HttpUrl url, long startRange, long endRange, List<Cookie> jar) throws IOException {
		return get(url, startRange, endRange, jar, Headers.of(new HashMap<>()));
	}

	default Response get(HttpUrl url, long startRange, long endRange) throws IOException {
		return get(url, startRange, endRange, new ArrayList<>(), Headers.of(new HashMap<>()));
	}

	default Response get(HttpUrl url, long startRange, List<Cookie> jar) throws IOException {
		return get(url, startRange, jar, Headers.of(new HashMap<>()));
	}

	default Response get(HttpUrl url, long startRange) throws IOException {
		return get(url, startRange, new ArrayList<>(), Headers.of(new HashMap<>()));
	}
	//-----------------------------------------------------------------------------//

    

    default Response get(HttpUrl url, Headers headers) throws IOException {
    	return get(url, new ArrayList<>(), headers);
    }

//    default Response get(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
//    	return get(url, 0, jar, headers);
//    }

    default Response get(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
    	return response(getCall(url, jar, headers));
    }

    default Response get(HttpUrl url, long startRange, long endRange, Headers headers) throws IOException {
        return get(url, startRange, endRange, new ArrayList<>(), headers);
    }

    

    default Response get(HttpUrl url, long startRange, Headers headers) throws IOException {
        return get(url, startRange, new ArrayList<>(), headers);
    }
    
    default Response get(Item item) throws IOException {
        return response(getCall(item, 0));
    }
    
    default Response get(Item item, int index) throws IOException {
        return response(getCall(item, index));
    }
    
    default Call getCall(Item item, int index) throws IOException {
    	RangeInfo info = item.getRangeInfo();
        return getCall(item, info.indexOf(index)[0],info.indexOf(index)[1]);
    }
    
    default Response get(Item item, long startRange, long endRange) throws IOException {
        return response(getCall(item, startRange, endRange));
    }
    default Call getCall(Item item, long startRange, long endRange) throws IOException {
        return getCall(item.getUpdateUrl(), 
        		startRange, 
        		endRange, 
        		item.getCookies(), 
        		item.getHeaders());
    }
    
    default Response get(HttpUrl url, long startRange, long endRange, List<Cookie> jar, Headers headers) throws IOException {
        if(endRange == -1) return get(url, startRange, jar, headers);
    	return response(getCall(url, startRange, endRange, jar, headers));
    }
    
   
    
    default Response get(HttpUrl url, long startRange, List<Cookie> jar, Headers headers) throws IOException {
    	return response(getCall(url, startRange, jar, headers));
    }
    
    default Call getCall(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .headers(headers)
                .get();
        getHttpClient().cookieJar().saveFromResponse(url, jar);
        return newCall(builder.build());
    }
    
    default Call getCall(HttpUrl url, long startRange, List<Cookie> jar, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .headers(headers)
                .addHeader("Range", "bytes=" + startRange + "-")
                .get();
                //if( startRange > 0) builder.addHeader("Range", "bytes=" + startRange + "-");
        getHttpClient().cookieJar().saveFromResponse(url, jar);
        return newCall(builder.build());
    }
    
    default Response getStream(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
    	return response(getStreamCall(url, jar, headers));
    }
    default Call getStreamCall(HttpUrl url, List<Cookie> jar, Headers headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .headers(headers)
                .get();
                //if( startRange > 0) builder.addHeader("Range", "bytes=" + startRange + "-");
        getHttpClient().cookieJar().saveFromResponse(url, jar);
        return newCall(builder.build());
    }
    
    
    
    default Call getCall(HttpUrl url, long startRange, long endRange, List<Cookie> jar, Headers headers) throws IOException {
    	if(endRange == -1) return getCall(url, startRange, jar, headers);
    	Request.Builder builder = new Request.Builder()
                .get()
                .url(url)
                .headers(headers)
//                .addHeader("Range", "bytes=" + startRange + "-" + endRange) ;
                .addHeader("Range", "bytes=" + startRange + "-" )
                //.addHeader("Cache-Control", "no-cache")
                //.addHeader("Pragma", "no-cache")
                //.addHeader("Connection", "Keep-Alive")
                //.addHeader("User-Agent", "okaria/1.88.0 (java)")
               ;
    	
    	
//    	if(startRange == 0) {
//    		// nothing
//    	}
//    	else //if(endRange != -1 ) 
//    		{
//    		builder.addHeader("Range", "bytes=" + startRange + "-" + endRange);
//    	}
////    	else {
////    		builder.addHeader("Range", "bytes=" + startRange + "-");
////    	}
    	
    	/*
    	 * Cache-Control: no-cache
    	 * Pragma: no-cache
    	 * Connection: Keep-Alive
    	 */
        getHttpClient().cookieJar().saveFromResponse(url, jar);
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
    	builder.append('\n');
    	builder.append('\n');
    	Log.fine(getClass(), "Client Request / Server Respnse", builder.toString());
    }
}
