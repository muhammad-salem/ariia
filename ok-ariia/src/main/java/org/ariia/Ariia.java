package org.ariia;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCli;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.okhttp.OkClient;
import org.terminal.console.log.Level;

public class Ariia {

    public static void main(String[] args) {

        Argument arguments = new Argument(args);
        if (arguments.isEmpty() || arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        } else if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - OkHttp (3.14.7)");
            return;
        }

        LogCli.initLogServices(arguments, Level.log);
        Properties properties = new Properties(arguments);
        Runnable onComplete = () -> {
            if (!arguments.isDaemonService()) {
                System.exit(0);
            }
        };
        AriiaCli cli = new AriiaCli(Clients.segmentClient(properties, new OkClient(arguments.getProxy())), onComplete);
        cli.lunchAsCliApp(arguments, properties);

    }

}
