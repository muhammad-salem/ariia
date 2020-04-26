package org.ariia.web;

import java.io.IOException;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCli;
import org.ariia.core.api.client.Clients;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.logging.Log;
import org.ariia.mvc.WebServer;
import org.ariia.okhttp.OkClient;
import org.ariia.web.controller.ItemController;
import org.ariia.web.services.ItemService;
import org.terminal.console.log.Level;

public class WebApp {

	public static boolean isRunningFromJar() {
		return WebApp.class.getResource("WebApp.class").getProtocol().equalsIgnoreCase("jar");
	}

	public static void main(String[] args) throws IOException {
		Argument arguments = new Argument(args);
		if (arguments.isHelp()) {
			System.out.println(TerminalArgument.help());
			return;
		} else if (arguments.isVersion()) {
			System.out.println("Ariia WEB APP version '0.2.7'");
			return;
		}

		LogCli.initLogServices(arguments, Level.info);
		AriiaCli cli = new AriiaCli( 
				(v)-> Clients.segmentClient(new OkClient(arguments.getProxy())));
		cli.lunch(arguments);

		int port = arguments.isServerPort() ? arguments.getServerPort() : 8080;
		String resourceLocation = arguments.isServerResourceLocation() ? arguments.getServerResourceLocation()
				: "/static/angular";
		WebServer.ResourceType type = arguments.isServerResourceLocation() ? WebServer.ResourceType.FILE :
//        			isRunningFromJar() ? 
//        					WebServer.ResourceType.IN_MEMORY : 
				WebServer.ResourceType.STREAM;
		Log.log(WebApp.class, "Running Web Server",
				String.format("start Port: %d, Path: %s, Resource Location type: %s", port, resourceLocation, type));
		WebServer server = new WebServer(port, resourceLocation, type);

		ServiceManager manager = cli.getServiceManager();
		ItemController controller = new ItemController(new ItemService(manager));
		server.createControllerContext(controller);
		server.start();

	}

}
