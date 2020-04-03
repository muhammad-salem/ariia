package org.ariia;

import java.io.IOException;

import org.ariia.web.WebServer;

public class WebApp {

   public static boolean isRunningFromJar(){
       return WebApp.class.getResource("WebApp.class").getProtocol().equalsIgnoreCase("jar");
   }

	public static void main(String[] args) throws IOException {
        int port = 8080;
        String resourceLocation = args.length > 0 ? 
        		args[0] : "/static/angular";
        WebServer.ResourceType type = args.length > 0 ? WebServer.ResourceType.FILE
                : isRunningFromJar() ? WebServer.ResourceType.IN_MEMORY : WebServer.ResourceType.STREAM;
        System.out.printf("port: %d, location: %s, type: %s\n", port, resourceLocation, type);
        WebServer server = new WebServer(port, resourceLocation, type);
        server.start();
    }

}
