package org.ariia.mvc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

import org.ariia.mvc.model.ControllerHandler;
import org.ariia.mvc.processing.ProxySwitcher;
import org.ariia.mvc.resource.FileResourceHandler;
import org.ariia.mvc.resource.InMemoryResourceHandler;
import org.ariia.mvc.resource.MultiRootResourceHandler;
import org.ariia.mvc.resource.ResourceHandler;
import org.ariia.mvc.sse.ServerSideEventHandler;
import org.ariia.mvc.sse.SourceEvent;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WebServer {
	
	public enum ResourceType {
		STREAM,
		IN_MEMORY,
		FILE,
		JAR_MULTI
	}
	
	private HttpServer server;
	private String staticResourceHandler;
	
	public WebServer(int port) throws IOException {
		this(new InetSocketAddress(port));
	}
	
	public WebServer(int port, String resourceLocation) throws IOException {
		this(port, resourceLocation, false);
	}
	
	public WebServer(int port, String resourceLocation, boolean inMemory) throws IOException {
		this(new InetSocketAddress(port), resourceLocation, inMemory);
	}
	
	public WebServer(int port, String resourceLocation, ResourceType type) throws IOException {
		this(new InetSocketAddress(port), resourceLocation, type);
	}
	
	public WebServer(InetSocketAddress address) throws IOException {
		this(address, null);
	}
	
	public WebServer(InetSocketAddress address, String resourceLocation) throws IOException {
		this(address, resourceLocation, false);
	}
	
	public WebServer(InetSocketAddress address, String resourceLocation, boolean inMemory) throws IOException {
		this.server  = HttpServer.create(address, 0);
		this.staticResourceHandler = resourceLocation;
		if (inMemory) {
			this.createInMemoryResourceContext("/", resourceLocation);
		} else {
			this.createResourceContext("/", resourceLocation);
		}
	}
	
	public WebServer(InetSocketAddress address, String resourceLocation, ResourceType type) throws IOException {
		this.server  = HttpServer.create(address, 0);
		this.staticResourceHandler = resourceLocation;
		switch (type) {
			case STREAM: {
			this.createResourceContext("/", resourceLocation);
			break;
		}
			case IN_MEMORY: {
			this.createInMemoryResourceContext("/", resourceLocation);
			break;
		}
			case FILE: {
			this.createFileResourceContext("/", resourceLocation);
			break;
		}
			case JAR_MULTI:
			default:
			this.createMultiResourceContext("/", resourceLocation);
		}
	}

	public HttpServer server() {
		return server;
	}
	
	public String staticResourceHandler() {
		return staticResourceHandler;
	}
	
	public void removeResourceContext() throws IllegalArgumentException {
		server.removeContext("/");
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
	
	public HttpContext createMultiResourceContext(String path, String... resourceLocations) {
		HttpHandler handler = new MultiRootResourceHandler(resourceLocations);
		return server.createContext(path, handler);
	}
	
	public HttpContext createInMemoryResourceContext(String path, String resourceLocation) {
		HttpHandler handler = new InMemoryResourceHandler(resourceLocation);
		return server.createContext(path, handler);
	}
	
	public HttpContext createFileResourceContext(String path, String resourceLocation) {
		HttpHandler handler = new FileResourceHandler(resourceLocation);
		return server.createContext(path, handler);
	}
	
	public HttpContext createContext(String path) {
		return server.createContext(path);
	}
	
	public HttpContext createControllerContext(Object controller) {
		ProxySwitcher switcher = new ProxySwitcher(controller);
		ControllerHandler handler = new ControllerHandler(controller, switcher);
		return server.createContext(switcher.getContext(), handler);
	}
	
	public HttpContext createServerSideEventContext(String path, ServerSideEventHandler handler) {
		return server.createContext(path, handler);
	}
	
	/**
	 * @param path server context
	 * @param event an EventBroadcast
	 * @return HttpContext
	 */
	public HttpContext createServerSideEventContext(String path, SourceEvent event) {
		ServerSideEventHandler handler = new ServerSideEventHandler(event);
		return server.createContext(path, handler);
	}
	
}
