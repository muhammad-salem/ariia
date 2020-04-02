package org.ariia.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WebServer {
	
	private HttpServer server;
	private String staticResourceHandler;
	
	public WebServer(int port) throws IOException {
		this(new InetSocketAddress(port));
	}
	
	public WebServer(InetSocketAddress address) throws IOException {
		this(address, null);
	}
	
	public WebServer(InetSocketAddress address, String resourceLocation) throws IOException {
		this.server  = HttpServer.create(address, 0);
		this.staticResourceHandler = resourceLocation;
		this.createResourceContext("/", resourceLocation);
	}

	public HttpServer server() {
		return server;
	}
	
	public String staticResourceHandler() {
		return staticResourceHandler;
	}
	
	public void start() {
		server.start();
	}

	public void setExecutor(Executor executor) {
		server.setExecutor(executor);
	}

	public void stop(int delay) {
		server.stop(delay);
	}

	public HttpContext createContext(String path, HttpHandler handler) {
		return server.createContext(path, handler);
	}
	
	public HttpContext createResourceContext(String path, String resourceLocation) {
		HttpHandler handler = new ResourceHandler(resourceLocation);
		return server.createContext(path, handler);
	}
	
	
	public HttpContext createContext(String path) {
		return server.createContext(path);
	}
}