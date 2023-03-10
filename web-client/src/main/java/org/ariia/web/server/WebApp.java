package org.ariia.web.server;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.internal.JavaHttpClient;
import org.ariia.web.WebService;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class WebApp {

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        var arguments = new Argument(args);
        if (arguments.isHelp()) {
            System.out.println(TerminalArgument.help());
            return;
        } else if (arguments.isVersion()) {
            System.out.println(arguments.getVersion() + " - Angular Material (11.0.9)");
            return;
        }
        var client = new JavaHttpClient(arguments.getProxy(), arguments.isInsecure());
        WebService.start(arguments, client);
    }

}
