package org.ariia.web;

import org.ariia.args.Argument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCLI;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.core.api.client.SegmentClient;
import org.ariia.core.api.request.ClientRequest;
import org.ariia.logging.Log;
import org.ariia.mvc.WebServer;
import org.ariia.mvc.WebServer.ResourceType;
import org.ariia.mvc.router.Routes;
import org.ariia.mvc.sse.EventBroadcast;
import org.ariia.web.app.WebDownloadService;
import org.ariia.web.app.WebLoggerPrinter;
import org.ariia.web.controller.ItemController;
import org.ariia.web.controller.LogLevelController;
import org.ariia.web.controller.SettingController;
import org.ariia.web.services.ItemService;
import org.ariia.web.services.SettingService;
import org.terminal.console.log.Level;
import org.terminal.console.log.api.Printer;
import org.terminal.console.log.impl.PrinterImpl;
import org.terminal.strings.AnsiStringBuilder;

import java.io.IOException;

public class WebService {

    public static boolean isRunningFromJar() {
        return WebService.class.getResource("WebService.class").getProtocol().equalsIgnoreCase("jar");
    }

    public static void start(Argument arguments, ClientRequest clientRequest) throws IOException {

        // setup logging service
        var mainBroadcast = new EventBroadcast();
        var printer = new PrinterImpl(System.out);
        var loggingPrinter = new WebLoggerPrinter(mainBroadcast, printer);
        LogCLI.initLogServicesNoStart(arguments, loggingPrinter, Level.info);
        var properties = new Properties(arguments);

        // setup web server
        var port = arguments.isServerPort() ? arguments.getServerPort() : 8080;
        var resourceLocation = arguments.isServerResourceLocation()
                ? arguments.getServerResourceLocation()
                : "/static/angular";
        WebServer.ResourceType type = arguments.isServerResourceLocation()
                ? WebServer.ResourceType.FILE
//        			: isRunningFromJar()
//        			? WebServer.ResourceType.IN_MEMORY :
                : WebServer.ResourceType.STREAM;


        var homeRoutes = new Routes("home");
        var dashboardRoutes = new Routes("dashboard");
        var downloadRoutes = new Routes("download", "**");
        var networkRoutes = new Routes("network", "monitor", "chart");
        var configRoutes = new Routes("config");
        var loggerRoutes = new Routes("logger");

        var rootRoutes = new Routes(
                "/",
                homeRoutes,
                dashboardRoutes,
                downloadRoutes,
                networkRoutes,
                configRoutes,
                loggerRoutes
        );

        var server = new WebServer(port, resourceLocation, type, rootRoutes);

        // setup download manager service
        var client = Clients.segmentClient(properties, clientRequest);
        var downloadService = new WebDownloadService(mainBroadcast);

        var cli = new AriiaCli(downloadService, client);

        var settingController = new SettingController(new SettingService(downloadService, properties));
        server.createControllerContext(settingController);

        var itemController = new ItemController(new ItemService(downloadService));
        server.createControllerContext(itemController);
        var logLevelController = new LogLevelController();
        server.createControllerContext(logLevelController);

        server.createServerSideEventContext("/backbone-broadcast", mainBroadcast);

        cli.lunchAsWebApp(arguments, properties);
        server.start();
        LogCLI.startLogService();
        var log = new AnsiStringBuilder();
        log.append("start local web server: ");
        log.blueLite().blink();
        log.build();
        log.append("http://127.0.0.1:" + port + "/");
        if (type == ResourceType.FILE) {
            log.defaultColor();
            log.build();
            log.append("\nStreaming Directory: ");
            log.redLite();
            log.append(resourceLocation);
        }

        Log.log(WebService.class, "Running Web Server", log.toString());


    }

}
