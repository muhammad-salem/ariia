package org.ariia.mvc.router;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;


/**
 * for RESTFulContext main could will be "/api/v1" or "/api/v2"
 * to add router use pattern like as:
 * "/student/edit/:id"
 * <p>
 * so the full URL to this service "/api/v2/student/edit/:id"
 *
 * @author salem
 */
public class RESTFulContext implements HttpHandler {

    protected String context;
    protected HttpServer server;

    protected Set<RESTfulRouter> restfulRouters;


    /**
     * use '/' as default constructor
     *
     * @param server
     */
    public RESTFulContext(HttpServer server) {
        this("/", server);
    }

    public RESTFulContext(String context, HttpServer server) {
        this.context = Objects.requireNonNull(context);
        this.server = Objects.requireNonNull(server);
        this.server.createContext(context, this);
        this.restfulRouters = new HashSet<>();
    }

    /**
     * Subscriber Router
     *
     * @param router
     * @param method
     * @return
     */
    protected RESTfulRouter subscribeRouter(String router, String method) {
        String regex = '^' + router.replaceAll(":(?:.[a-zA-Z0-9]*)", "(?:.*)");
        RESTfulRouter resTfulRouter = new RESTfulRouter(this, method, router, regex);
        restfulRouters.add(resTfulRouter);
        return resTfulRouter;
    }

    public RESTfulRouter get(String router) {
        return subscribeRouter(router, "GET");
    }

    public RESTfulRouter head(String router) {
        return subscribeRouter(router, "HEAD");
    }

    public RESTfulRouter post(String router) {
        return subscribeRouter(router, "POST");
    }

    public RESTfulRouter put(String router) {
        return subscribeRouter(router, "PUT");
    }

    public RESTfulRouter delete(String router) {
        return subscribeRouter(router, "DELETE");
    }

    public RESTfulRouter patch(String router) {
        return subscribeRouter(router, "PATCH");
    }

    public RESTfulRouter options(String router) {
        return subscribeRouter(router, "OPTIONS");
    }

    public RESTfulRouter trace(String router) {
        return subscribeRouter(router, "TRACE");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String uri = exchange.getRequestURI().toString();
        String contextPath = uri.replaceFirst(context, "");
        Stream<RESTfulRouter> stream = restfulRouters.stream();
        stream.filter(restful -> {
            return exchange.getRequestMethod().equalsIgnoreCase(restful.getMethod()) || exchange.getRequestMethod().equalsIgnoreCase("*");
        })
                .filter(restful -> {
                    return contextPath.matches(restful.getRegex());
                })
                .limit(1l)
                .forEach(restful -> {
                    try {
                        restful.getHandler().handle(exchange);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }


}
