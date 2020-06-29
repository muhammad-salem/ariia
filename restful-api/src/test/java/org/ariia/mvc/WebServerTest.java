package org.ariia.mvc;

import org.ariia.mvc.model.ContextActionHandler;
import org.ariia.mvc.model.ControllerHandler;
import org.ariia.mvc.processing.ProxySwitcher;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class WebServerTest {

    public static void main(String[] args) throws IOException {
        new WebServerTest().testWebServer();
    }

    @Test
    private void testWebServer() {
        try {
            int port = 8080;
            String resourceLocation = "/static/angular";
            WebServer.ResourceType type = WebServer.ResourceType.FILE;
            System.out.printf("port: %d, location: %s, type: %s\n", port, resourceLocation, type);
            WebServer server = new WebServer(port, resourceLocation, type);
            server.createContext("/context/", new ContextActionHandler<>("/context/"));

            ItemController test = new ItemController();
            ProxySwitcher switcher = new ProxySwitcher(test);
            ControllerHandler handler = new ControllerHandler(test, switcher);
            server.createContext(switcher.getContext(), handler);

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
