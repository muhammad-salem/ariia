package org.ariia.mvc.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public interface StreamHandler {
	
	
	
	default void handelStream(HttpExchange exchange, String filename, InputStream stream) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.put("Content-Type", Collections.singletonList(MimeType.getMimeForFileName(filename)));
	    
		Headers requestHeaders = exchange.getRequestHeaders();
		final OutputStream responseBody = exchange.getResponseBody();
//		boolean isGzip = false;
		
		
		
		for (String	encoding: requestHeaders.getOrDefault("Accept-Encoding", new ArrayList<>(0))) {
	    	if (Objects.isNull(encoding)) {
				continue;
			}
			if (encoding.contains("gzip") ) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(out);
				byte[] bs = new byte[1024];
			    int read = 0;
			    while ( (read = stream.read(bs)) > 0) {
			    	gzip.write(bs, 0, read);
				}
			    gzip.flush();
			    gzip.close();
			    stream = new ByteArrayInputStream(out.toByteArray());
			    responseHeaders.put("Content-Encoding", Collections.singletonList("gzip"));
			    out.close();
		    	break;
			}
		}
		
	    
	    int responseLength = stream.available();
	    byte[] bs = new byte[1024];
	    int read = 0;
	    exchange.sendResponseHeaders(200, responseLength);
	    while ( (read = stream.read(bs)) > 0) {
			responseBody.write(bs, 0, read);
		}
	    responseBody.flush();
	    responseBody.close();
	    stream.close();
	    exchange.close();
	}

}
