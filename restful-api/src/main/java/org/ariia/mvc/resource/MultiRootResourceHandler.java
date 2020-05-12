package org.ariia.mvc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MultiRootResourceHandler implements HttpHandler, StreamHandler {
	String[] resourceLocations;
	
	public MultiRootResourceHandler() {
		this(new String[] {"/static"});
	}
	
	public MultiRootResourceHandler(String[] resourceLocations) {
		this.resourceLocations = 
				Objects.requireNonNull(resourceLocations, "resource locations must not be empty");
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.getPath();
		if (filename.equals("/")) {
			filename = "/index.html";
		}
		InputStream stream = null;
		for (String resourceLocation : resourceLocations) {
			stream = getClass().getResourceAsStream(resourceLocation + filename);
			if (Objects.nonNull(stream)) {
				break;
			}
		}
		
		if (Objects.isNull(stream)) {
			exchange.sendResponseHeaders(404, -1);
			return;
		}

	    handelStream(exchange, filename, stream);
	}
}