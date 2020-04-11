package org.ariia.web;

import java.io.IOException;

import org.ariia.AriiaCli;
import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.core.api.client.Clients;
import org.ariia.internal.AriiaHttpClient;
import org.ariia.mvc.WebServer;
import org.ariia.mvc.model.ContextActionHandler;


public class WebApp {

   public static boolean isRunningFromJar(){
       return WebApp.class.getResource("WebApp.class").getProtocol().equalsIgnoreCase("jar");
   }

	public static void main(String[] args) throws IOException {
		Argument arguments = new Argument(args);
		if (arguments.isEmpty() || arguments.isHelp()) {
			System.out.println(TerminalArgument.help());
			return;
		} else if (arguments.isVersion()) {
			System.out.println("Ariia WEB APP version '0.2.6'");
			return;
		}
		
        int port = arguments.isServerPort() ? arguments.getServerPort() : 8080;
        String resourceLocation = arguments.isServerResourceLocation() ? 
        		arguments.getServerResourceLocation() : "/static/angular";
        WebServer.ResourceType type = arguments.isServerResourceLocation() ?
        		WebServer.ResourceType.FILE : 
        			isRunningFromJar() ? 
        					WebServer.ResourceType.IN_MEMORY 
        					: WebServer.ResourceType.STREAM;
        System.out.printf("port: %d, location: %s, type: %s\n", port, resourceLocation, type);
        WebServer server = new WebServer(port, resourceLocation, type);
        server.createContext("/context/", new ContextActionHandler<>("/context/"));
        server.start();
        
		
		AriiaCli cli = new AriiaCli((v)-> { 
				return Clients.segmentClient(new AriiaHttpClient(arguments.getProxy()));
			}
		);
		cli.setFinishAction(() -> {
			if (!arguments.isDaemonService()){
				System.exit(0);
			}
		});
		cli.lunch(arguments);
    }

}
