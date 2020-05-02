package org.ariia.mvc.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FileRedirectResourceHandler implements HttpHandler, StreamHandler {
	
	private String resourceLocation;
	
	public FileRedirectResourceHandler() {
		this("/static");
	}
	
	public FileRedirectResourceHandler(String resourceLocation) {
		this.resourceLocation = Objects.requireNonNull(resourceLocation);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI uri = exchange.getRequestURI();
		String filename = uri.getPath();
		if (filename.equals("/") || filename.startsWith("/redirect") ) {
			filename = "/index.html";
		}
		
		FileInputStream stream;
		try {
			stream = new FileInputStream(resourceLocation + filename);
		} catch (FileNotFoundException e) {
			Headers headers = exchange.getResponseHeaders();
			String url = "/redirect" + uri.toString();
			headers.add("Location", url);
			exchange.sendResponseHeaders(301, -1);
			exchange.close();
			return;
		}

	    handelStream(exchange, filename, stream);
	}

}