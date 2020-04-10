package org.ariia.mvc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourceHandler implements HttpHandler {
	String resourceLocation;
	
	public ResourceHandler() {
		this(null);
	}
	
	public ResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.requireNonNullElse(resourceLocation, "/static");
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.toString();
		if (filename.equals("/")) {
			filename = "/index.html";
		}
		InputStream stream = getClass().getResourceAsStream(resourceLocation + filename);
		if (stream == null) {
			exchange.sendResponseHeaders(404, -1);
			return;
		}

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