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

public class FileResourceHandler implements HttpHandler, StreamHandler {
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
		String filename = uri.getPath();
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

	    handelStream(exchange, filename, stream);
	}
}