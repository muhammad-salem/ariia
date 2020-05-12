package org.ariia.mvc.resource;

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
	
	default void handelStreamAndSetFileName(HttpExchange exchange, String filename, InputStream stream) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		filename = "attachment; filename=\"" + filename + "\"";
		responseHeaders.put("Content-Disposition", Collections.singletonList(filename));
		
		handelStream(exchange, filename, stream);
	}
	
	default void handelStream(HttpExchange exchange, String filename, InputStream stream) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.put("Content-Type", Collections.singletonList(MimeType.getMimeForFileName(filename)));
	    
		Headers requestHeaders = exchange.getRequestHeaders();
		final OutputStream responseBody = exchange.getResponseBody();
		boolean supportGZip = false;
		for (String	encoding: requestHeaders.getOrDefault("Accept-Encoding", new ArrayList<>(0))) {
	    	if (Objects.isNull(encoding)) {
				continue;
			}
			if (encoding.contains("gzip") ) {
				supportGZip = true;
		    	break;
			}
		}
		
		if (!supportGZip) {
			writeToOutputStream(exchange, stream);
		} else {
			// 2MB
			if( (stream.available() - (2*1024*1024) ) < 0) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPOutputStream gzip = new GZIPOutputStream(out);
				byte[] bs = new byte[512];
			    int read = 0;
			    while ( (read = stream.read(bs)) > 0) {
			    	gzip.write(bs, 0, read);
				}
			    gzip.flush();
			    gzip.close();
			    writeGZipStream(exchange, out.toByteArray(), responseHeaders);
			} else {
				writeGZipStream(exchange, stream, responseBody, responseHeaders);
			}
		}
		
	    stream.close();
	    exchange.close();
	}

	default void writeToOutputStream(HttpExchange exchange, InputStream stream) throws IOException {
	    exchange.sendResponseHeaders(200, stream.available());
		byte[] bs = new byte[512];
	    int read = 0;
	    while ( (read = stream.read(bs)) > 0) {
	    	exchange.getResponseBody().write(bs, 0, read);
		}
	    exchange.getResponseBody().flush();
	}
	
	default void writeGZipStream(HttpExchange exchange, byte[] bytes, Headers responseHeaders) throws IOException {
	    responseHeaders.put("Content-Encoding", Collections.singletonList("gzip"));
	    exchange.sendResponseHeaders(200, bytes.length);
	    exchange.getResponseBody().write(bytes, 0, bytes.length);
	    exchange.getResponseBody().flush();
	}
	
	default void writeGZipStream(HttpExchange exchange, final InputStream stream,
			final OutputStream responseBody, Headers responseHeaders) throws IOException {
	    responseHeaders.put("Content-Encoding", Collections.singletonList("gzip"));
	    exchange.sendResponseHeaders(200, 0);
	    GZIPOutputStream gzip = new GZIPOutputStream(responseBody);
		byte[] bs = new byte[512];
	    int read = 0;
	    while ( (read = stream.read(bs)) > 0) {
	    	gzip.write(bs, 0, read);
	    	gzip.flush();
		}
	    gzip.close();
	}

}