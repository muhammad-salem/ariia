package org.ariia.mvc.model;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ContextActionHandler<T> implements HttpHandler {

    protected final String context;

    public ContextActionHandler(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }


    /**
     * method:{body?} ->  {context}/{action}/{id?}
     */

    @Override
    public void handle(HttpExchange exchange) {
        String uri = exchange.getRequestURI().toString();
        System.out.println("uri: " + uri);
//		URI uri2 =  exchange.getRequestURI();
//		InputStream requestBody = exchange.getRequestBody();
        String method = exchange.getRequestMethod();
        if (method.equalsIgnoreCase("get")) {
            get(exchange);
        } else if (method.equalsIgnoreCase("post")) {
            post(exchange);
        }
    }

    private void get(HttpExchange exchange) {
        exchange.close();
    }

    private void post(HttpExchange exchange) {
        exchange.close();
    }

}