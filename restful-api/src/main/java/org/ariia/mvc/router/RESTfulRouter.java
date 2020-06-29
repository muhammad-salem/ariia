package org.ariia.mvc.router;

import com.sun.net.httpserver.HttpHandler;

import java.util.Objects;

public class RESTfulRouter {

    String method;
    String router;
    String regex;

    RESTFulContext restFulContext;

    HttpHandler handler = (exchange) -> {
        exchange.close();
    };

    public RESTfulRouter(RESTFulContext restFulContext, String method, String router, String regex) {
        this.restFulContext = Objects.requireNonNull(restFulContext);
        this.method = Objects.requireNonNull(method);
        this.router = Objects.requireNonNull(router);
        this.regex = Objects.requireNonNull(regex);
//		this.handlers = new HashSet<>();
    }

    public String getMethod() {
        return method;
    }

    public String getRouter() {
        return router;
    }

    public String getRegex() {
        return regex;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RESTfulRouter)) {
            return false;
        }
        RESTfulRouter other = (RESTfulRouter) obj;
        return regex.equals(other.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, router, regex);
    }

    public RESTFulContext subscribe(HttpHandler handler) {
        this.handler = handler;
        return restFulContext;
    }

}
