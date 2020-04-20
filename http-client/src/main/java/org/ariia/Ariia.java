package org.ariia;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.core.api.client.Clients;
import org.ariia.internal.AriiaHttpClient;

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
		AriiaCli.initLogServices(arguments);
		
		AriiaCli cli = new AriiaCli((v)-> { 
				return Clients.segmentClient(new AriiaHttpClient(arguments.getProxy()));
			}
		);
		cli.setFinishAction(() -> {
			if (!arguments.isDaemonService()){
				System.exit(0);
			}
		});
		cli.lunch(arguments);
		
	}

}
