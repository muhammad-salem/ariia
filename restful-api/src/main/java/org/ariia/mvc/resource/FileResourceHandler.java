package org.ariia.mvc.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileResourceHandler implements HttpHandler {
	String resourceLocation;
	
	public FileResourceHandler() {
		this(null);
	}
	
	public FileResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.isNull(resourceLocation)? "/static" : resourceLocation;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		exchange.getRequestURI();
		URI uri = exchange.getRequestURI();
		String filename = uri.toString();
		if (filename.equals("/")) {
			filename = "/index.html";
		}
		FileInputStream stream;
		try {
			stream = new FileInputStream(resourceLocation + filename);
		} catch (FileNotFoundException e) {
			exchange.sendResponseHeaders(404, -1);
			exchange.close();
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