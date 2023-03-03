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

        Argument arguments = new Argument(args);
        if (arguments.isEmpty() || arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        } else if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - JDK (11+)");
            return;
        }

        LogCLI.initLogServices(arguments, Level.log);
        Properties properties = new Properties(arguments);
        Runnable onComplete = () -> {
            if (!arguments.isDaemonService()) {
                System.exit(0);
            }
        };
        AriiaCli cli = new AriiaCli(Clients.segmentClient(properties, new JavaHttpClient(arguments.getProxy(), arguments.isInsecure())), onComplete);
        cli.lunchAsCliApp(arguments, properties);

    }

}