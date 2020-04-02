package org.ariia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.ariia.web.WebServer;

public class WebApp {

	public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        int port = 8080;
        
        WebServer server = new WebServer(port);
        server.createContext("/iii", ex -> {
        	ex.sendResponseHeaders(200, 2);
        	ex.getResponseBody().write("hi".getBytes(StandardCharsets.UTF_8));
        });
        
        server.start();
        System.out.println("nano started in: " + (System.currentTimeMillis() - start) + "ms at port: " + port);
    }

}
