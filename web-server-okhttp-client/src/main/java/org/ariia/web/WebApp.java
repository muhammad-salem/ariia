package org.ariia.web;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCli;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.core.api.client.SegmentClient;
import org.ariia.logging.Log;
import org.ariia.mvc.WebServer;
import org.ariia.mvc.WebServer.ResourceType;
import org.ariia.mvc.router.Routes;
import org.ariia.mvc.sse.EventBroadcast;
import org.ariia.okhttp.OkClient;
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

public class WebApp {

    public static void main(String[] args) throws IOException {
        Argument arguments = new Argument(args);
        if (arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        } else if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - Angular Material (11.0.9)");
            return;
        }
        OkClient client = new OkClient(arguments.getProxy());
        WebService.start(arguments, client);
    }

}
