package org.ariia;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.cli.AriiaCli;
import org.ariia.cli.LogCli;
import org.ariia.core.api.client.Clients;
import org.ariia.internal.AriiaHttpClient;
import org.terminal.console.log.Level;

public class Ariia {
	
public static void main(String[] args) {
		
		Argument arguments = new Argument(args);
		if (arguments.isEmpty() || arguments.isHelp()) {
			System.out.println(TerminalArgument.help());
			return;
		} else if (arguments.isVersion()) {
			System.out.println("Ariia version '0.2.7'");
			return;
		}
		
		LogCli.initLogServices(arguments, Level.log);
		
		AriiaCli cli = new AriiaCli( 
				(v)-> Clients.segmentClient(new AriiaHttpClient(arguments.getProxy())), 
				()-> {
					if (!arguments.isDaemonService()){
						System.exit(0);
					}
				});
		cli.lunch(arguments);
		
	}

}
