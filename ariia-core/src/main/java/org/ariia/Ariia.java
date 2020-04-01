package org.ariia;

import java.util.Arrays;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.config.Properties;
import org.ariia.items.ItemBuilder;
import org.ariia.logging.Log;
import org.ariia.okhttp.service.ServiceManager;
import org.ariia.util.R;
import org.terminal.console.log.Level;

public class Ariia {

	public static void main(String[] args) {

		Argument arguments = new Argument(args);
		if (arguments.isEmpty() || arguments.isHelp()) {
			System.out.println(TerminalArgument.Help());
			return;
		} else if (arguments.isVersion()) {
			System.out.println("OKaria version \"0.2.35\"");
			return;
		}
		
		R.MK_DIRS(R.ConfigPath);

		String log_level = 
				arguments.getOrDefault(TerminalArgument.Debug, Level.info.name());
		Log.level(log_level);
		Log.trace(Ariia.class, "user input", Arrays.toString(args));
		Properties.Config(arguments);

		ServiceManager manager = ServiceManager.SegmentServiceManager(arguments.getProxy());
		manager.startScheduledService();
		
		Log.trace(Ariia.class, "Set Shutdown Hook Thread", "register shutdown thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			manager.runSystemShutdownHook();
			manager.printReport();
			System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
			
		}));
		
		ItemBuilder builder = new ItemBuilder(arguments);
		
		manager.initForDownload(builder.getItems());
		manager.setFinishAction(() ->  {
			System.exit(0);
		});

	}

}
