package org.ariia.web.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class InMemoryResourceHandler implements HttpHandler {
	String resourceLocation;
	private HashMap<String, byte[]> memory;
	
	public InMemoryResourceHandler() {
		this(null);
	}
	
	public InMemoryResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.requireNonNullElse(resourceLocation, "/static");
		this.memory = new HashMap<>();
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.toString();
		if (filename.equals("/")) {
			filename = "/index.html";
		}
		
		byte[] data = memory.get(filename);
		int responseLength = -1; // for not found --> 404
		if (Objects.isNull(data)) {
			InputStream stream = getClass().getResourceAsStream(resourceLocation + filename);
			if (Objects.isNull(stream)) {
				exchange.sendResponseHeaders(404, responseLength);
				return;
			}
			
			responseLength = stream.available();
			data = new byte[responseLength];
			int read = 0;
			do {
				read = stream.read(data, read, responseLength-read);
			} while (read > 0);
			stream.close();
			memory.put(filename, data);
		} else {
			responseLength = data.length;
		}
	    Headers headers = exchange.getResponseHeaders();
	    headers.put("Content-Type", Collections.singletonList(MimeType.getMimeForFileName(filename)));
	    exchange.sendResponseHeaders(200, responseLength);
		final OutputStream responseBody = exchange.getResponseBody();
	    responseBody.write(data);
	    responseBody.flush();
	    exchange.close();
	}
}