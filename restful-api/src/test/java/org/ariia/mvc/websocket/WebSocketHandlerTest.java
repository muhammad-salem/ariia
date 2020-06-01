package org.ariia.mvc.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class WebSocketHandlerTest {
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		WebSocketHandler webSocketHandler = new WebSocketHandler();
//		server.createContext("/", webSocketHandler);
		server.createContext("/ws", webSocketHandler);
		server.start();
	}
}
