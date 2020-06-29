package org.ariia.mvc;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.ariia.mvc.model.ControllerHandler;
import org.ariia.mvc.processing.ProxySwitcher;
import org.ariia.mvc.resource.FileResourceHandler;
import org.ariia.mvc.resource.StreamResourceHandler;
import org.ariia.mvc.router.Routes;
import org.ariia.mvc.sse.ServerSideEventHandler;
import org.ariia.mvc.sse.SourceEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class WebServer {

    private HttpServer server;
    private String staticResourceHandler;
    private List<ControllerHandler> controllerHandlers;

    public WebServer(int port, String resourceLocation, ResourceType type) throws IOException {
        this(new InetSocketAddress(port), resourceLocation, type, new Routes("/"));
    }

    public WebServer(int port, String resourceLocation, ResourceType type, Routes rootRoutes) throws IOException {
        this(new InetSocketAddress(port), resourceLocation, type, rootRoutes);
    }

    public WebServer(InetSocketAddress address, String resourceLocation, ResourceType type, Routes rootRoutes) throws IOException {
        this.server = HttpServer.create(address, 0);
        this.staticResourceHandler = resourceLocation;
        switch (type) {
            case FILE: {
                this.createFileResourceHandlerContext("/", resourceLocation, rootRoutes);
                break;
            }
            case STREAM:
            default: {
                this.createStreamResourceHandlerContext("/", resourceLocation, rootRoutes);
                break;
            }
        }
        this.controllerHandlers = new ArrayList<>();
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

    public HttpContext createStreamResourceHandlerContext(String path, String resourceLocation, Routes rootRoutes) {
        HttpHandler handler = new StreamResourceHandler(resourceLocation, rootRoutes);
        return server.createContext(path, handler);
    }

    public HttpContext createFileResourceHandlerContext(String path, String resourceLocation, Routes rootRoutes) {
        HttpHandler handler = new FileResourceHandler(resourceLocation, rootRoutes);
        return server.createContext(path, handler);
    }

    public HttpContext createContext(String path) {
        return server.createContext(path);
    }

    public HttpContext createControllerContext(Object controller) {
        ProxySwitcher switcher = new ProxySwitcher(controller);
        ControllerHandler handler = new ControllerHandler(controller, switcher);
        controllerHandlers.add(handler);
        return server.createContext(switcher.getContext(), handler);
    }

    public HttpContext createServerSideEventContext(String path, ServerSideEventHandler handler) {
        return server.createContext(path, handler);
    }

    /**
     * @param path  server context
     * @param event an EventBroadcast
     * @return HttpContext
     */
    public HttpContext createServerSideEventContext(String path, SourceEvent event) {
        ServerSideEventHandler handler = new ServerSideEventHandler(event);
        return server.createContext(path, handler);
    }

    public enum ResourceType {
        FILE, STREAM
    }

}
