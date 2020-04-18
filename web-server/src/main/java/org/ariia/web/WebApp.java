package org.ariia.web;

import java.io.IOException;

import org.ariia.AriiaCli;
import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.core.api.client.Clients;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.Builder;
import org.ariia.mvc.WebServer;
import org.ariia.okhttp.OkClient;
import org.ariia.web.controller.ItemController;
import org.ariia.web.services.ItemService;


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
		
		AriiaCli cli = new AriiaCli((v)-> { 
				return Clients.segmentClient(new OkClient(arguments.getProxy()));
			}
		);
		cli.lunch(arguments);
		
		
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
//        server.createContext("/context/", new ContextActionHandler<>("/context/"));
        
        

		ServiceManager manager = cli.getManager();
		ItemController controller = new ItemController(new ItemService(manager));
		server.createControllerContext(controller);
		server.start();
		
//		ItemBuilder builder = new ItemBuilder(arguments);
		String url = "http://releases.ubuntu.com/focal/ubuntu-20.04-beta-desktop-amd64.iso.torrent";
		Builder builder = new Builder(url);
		manager.download(builder.build());
		
    }

}
