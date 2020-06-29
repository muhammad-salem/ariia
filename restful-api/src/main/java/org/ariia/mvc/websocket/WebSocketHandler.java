package org.ariia.mvc.websocket;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocketHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String protocol = exchange.getProtocol();
//		String origin = exchange.getRequestHeaders().getFirst("Origin");


        System.out.println(method);
        System.out.println(exchange.getRequestURI());
        System.out.println(exchange.getProtocol());
        exchange.getRequestHeaders().forEach((key, list) -> {
            System.out.println(key + " " + list.toString());
        });

        if ("GET".equalsIgnoreCase(method) &&
                ("HTTP/1.1".equalsIgnoreCase(protocol) || "HTTP/2".equalsIgnoreCase(protocol))) {
            String secWebSocketKey = exchange.getRequestHeaders().getFirst("Sec-websocket-key");
//			String secWebSocketVersion = exchange.getRequestHeaders().getFirst("Sec-WebSocket-Version");

            String secWebSocketAccept = null;
            try {
                secWebSocketAccept = Base64.getEncoder().encodeToString(
                        MessageDigest.getInstance("SHA-1").digest((secWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                .getBytes("UTF-8")));
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(456, -1);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Connection", "Upgrade");
            exchange.getResponseHeaders().add("Upgrade", "websocket");
            exchange.getResponseHeaders().add("Sec-WebSocket-Accept", secWebSocketAccept);

            exchange.sendResponseHeaders(101, -1);
        } else {
            exchange.sendResponseHeaders(403, 0);
            exchange.close();
            return;
        }


    }

}