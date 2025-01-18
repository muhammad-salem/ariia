package org.ariia;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCLI;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Clients;
import org.ariia.internal.JavaHttpClient;
import org.terminal.console.log.Level;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Ariia {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {

        var arguments = new Argument(args);
        if (arguments.isEmpty() || arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        } else if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - JDK (21+)");
            return;
        }

        LogCLI.initLogServices(arguments, Level.log);
        var properties = new Properties(arguments);
        Runnable onComplete = () -> {
            if (!arguments.isDaemonService()) {
                System.exit(0);
            }
        };
        var httpClient = new JavaHttpClient(arguments.getProxy(), arguments.isInsecure());
        var client = Clients.segmentClient(properties, httpClient);
        var cli = new AriiaCli(client, onComplete);
        cli.lunchAsCliApp(arguments, properties);

    }

}
