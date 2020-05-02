package org.ariia.mvc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public interface StreamHandler {
	
	
	
	default void handelStream(HttpExchange exchange, String filename, InputStream stream) throws IOException {
		Headers headers = exchange.getResponseHeaders();
	    headers.put("Content-Type", Collections.singletonList(MimeType.getMimeForFileName(filename)));
	    
	    int responseLength = stream.available();
	    byte[] bs = new byte[1024];
	    int read = 0;
	    exchange.sendResponseHeaders(200, responseLength);
		final OutputStream responseBody = exchange.getResponseBody();
	    while ( (read = stream.read(bs)) > 0) {
			responseBody.write(bs, 0, read);
		}
	    responseBody.flush();
	    stream.close();
	    exchange.close();
	}

}
