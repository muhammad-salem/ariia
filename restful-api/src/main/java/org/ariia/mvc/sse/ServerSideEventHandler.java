package org.ariia.mvc.sse;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Objects;

/**
 * Legacy proxy servers are known to, in certain cases,
 * drop HTTP connections after a short timeout.
 * To protect against such proxy servers, authors can include a comment line
 * (one starting with a ‘:’ character) every 15 seconds or so.
 *
 * @author salem
 */

public final class ServerSideEventHandler implements HttpHandler {

    private final SourceEvent event;

    public ServerSideEventHandler(SourceEvent event) {
        this.event = Objects.requireNonNull(event);
    }

    public SourceEvent getEvent() {
        return event;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        String accept = exchange.getRequestHeaders().getFirst("Accept");
        if (!accept.equals("text/event-stream")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().add("Character-Encoding", "UTF-8");
        exchange.getResponseHeaders().add("Cache-Control", "no-cache");
        exchange.getResponseHeaders().add("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, 0);

        EventSubscriber subscriber = new EventSubscriber(exchange.getResponseBody());
        subscriber.message(MessageEvent.KeepAlive);
        event.subscribe(subscriber);
    }

}
