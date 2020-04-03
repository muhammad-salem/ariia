package org.ariia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.ariia.web.WebServer;

public class WebApp {

	public static void main(String[] args) throws IOException {
        int port = 8080;
        String resourceLocation = args.length >= 1 ? args[0] : "/static/angular";
        WebServer server = new WebServer(port, resourceLocation, args.length >= 1 ? 2 : 3);
        server.createContext("/iii", ex -> {
        	ex.sendResponseHeaders(200, 2);
        	ex.getResponseBody().write("hi".getBytes(StandardCharsets.UTF_8));
        });
        server.start();
    }

}
