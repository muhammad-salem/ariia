package org.ariia.mvc.router;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class RouterTest {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        RESTFulContext router = new RESTFulContext("/api/v2", server);
        server.start();

        router.get("/item/:id").subscribe(exchage -> {
//			System.out.println("RouterTest.main(): " + exchage.getRequestURI().toString());
            byte[] data = exchage.getRequestURI().toString().getBytes(StandardCharsets.UTF_8);
            exchage.sendResponseHeaders(200, data.length);
            exchage.getResponseBody().write(data);
            exchage.close();
        });

    }

}
