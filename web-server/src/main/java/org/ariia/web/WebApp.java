package org.ariia.web;

import java.io.IOException;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCli;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.core.api.client.SegmentClient;
import org.ariia.logging.Log;
import org.ariia.mvc.WebServer;
import org.ariia.mvc.sse.EventBroadcast;
import org.ariia.okhttp.OkClient;
import org.ariia.web.app.WebLoggerPrinter;
import org.ariia.web.app.WebServiceManager;
import org.ariia.web.controller.ItemController;
import org.ariia.web.controller.LogLevelController;
import org.ariia.web.services.ItemService;
import org.terminal.console.log.Level;
import org.terminal.console.log.api.Printer;
import org.terminal.console.log.impl.PrinterImpl;

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
		
		// setup logging service
		EventBroadcast mainBroadcast = new EventBroadcast();
		Printer printer = new PrinterImpl(System.out);
		WebLoggerPrinter loggingPrinter = new WebLoggerPrinter(mainBroadcast, printer);
		LogCli.initLogServicesNoStart(arguments, loggingPrinter, Level.info);
		Properties properties = new Properties(arguments);
		
		// setup web server
		int port = arguments.isServerPort() ? arguments.getServerPort() : 8080;
		String resourceLocation = arguments.isServerResourceLocation() ? arguments.getServerResourceLocation()
				: "/static/angular";
		WebServer.ResourceType type = arguments.isServerResourceLocation() ? WebServer.ResourceType.FILE :
//        			isRunningFromJar() ? 
//        					WebServer.ResourceType.IN_MEMORY : 
				WebServer.ResourceType.STREAM;
		WebServer server = new WebServer(port, resourceLocation, type, true);

		// setup download manager service
		SegmentClient client = Clients.segmentClient(properties, new OkClient(arguments.getProxy()));
		WebServiceManager serviceManager = new WebServiceManager(client, mainBroadcast);
		AriiaCli cli = new AriiaCli(serviceManager);
		
		
		ItemController itemController = new ItemController(new ItemService(serviceManager));
		server.createControllerContext(itemController);
		LogLevelController logLevelController = new LogLevelController();
		server.createControllerContext(logLevelController);
		
		server.createServerSideEventContext("/backbone-broadcast", mainBroadcast);
		
		cli.lunch(arguments, properties);
		server.start();
		LogCli.startLogService();
		Log.log(WebApp.class, "Running Web Server",
				String.format("start Port: %d, Path: %s, Resource Location type: %s", port, resourceLocation, type));
		

	}

}
