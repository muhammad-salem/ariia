package org.ariia.mvc.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RedirectResourceHandler implements HttpHandler, StreamHandler {
	
	private String resourceLocation;
	
	public RedirectResourceHandler() {
		this("/static");
	}
	
	public RedirectResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.requireNonNull(resourceLocation);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.getPath();
		if (filename.equals("/") || filename.startsWith("/redirect") ) {
			filename = "/index.html";
		}
		InputStream stream = getClass().getResourceAsStream(resourceLocation + filename);
		if (Objects.isNull(stream)) {
			Headers headers = exchange.getResponseHeaders();
			String url = "/redirect" + uri.toString();
			headers.add("Location", url);
			exchange.sendResponseHeaders(301, -1);
			return;
		}

	    handelStream(exchange, filename, stream);
	}

}