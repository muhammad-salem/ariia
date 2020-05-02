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

public class ResourceHandler implements HttpHandler, StreamHandler {
	String resourceLocation;
	
	public ResourceHandler() {
		this(null);
	}
	
	public ResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.isNull(resourceLocation)? "/static" : resourceLocation;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.getPath();
		if (filename.equals("/")) {
			filename = "/index.html";
		}
		InputStream stream = getClass().getResourceAsStream(resourceLocation + filename);
		if (stream == null) {
			exchange.sendResponseHeaders(404, -1);
			return;
		}

	    handelStream(exchange, filename, stream);
	}

}