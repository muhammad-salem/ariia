package org.ariia.mvc.resource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.ariia.mvc.router.Routes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

public abstract class RouterResourceHandler implements HttpHandler, StreamHandler {

    protected String indexFile;
    protected Routes routes;

    public RouterResourceHandler(String indexFile, Routes routes) {
        this.indexFile = Objects.requireNonNull(indexFile);
        this.routes = Objects.requireNonNull(routes);
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    public void setRootRoutes(Routes routes) {
        this.routes = routes;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String filename = uri.getPath();
        if(filename.contains("/../")){
        	exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }
        if (routes.lookupRoute(filename)) {
            filename = indexFile;
        }

        InputStream stream;
        try {
            stream = getResourceAsStream(filename);
        } catch (IOException e) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }

        handelStream(exchange, filename, stream);
    }

    protected abstract InputStream getResourceAsStream(String filename) throws IOException;

}
