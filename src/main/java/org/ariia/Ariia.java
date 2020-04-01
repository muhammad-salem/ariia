package org.ariia;

import java.util.Arrays;

import org.ariia.chrome.ChromeConnection;
import org.ariia.logging.Log;
import org.ariia.lunch.Argument;
import org.ariia.lunch.ItemBuilder;
import org.ariia.lunch.TerminalArgument;
import org.ariia.okhttp.service.ServiceManager;
import org.ariia.setting.Properties;
import org.ariia.util.R;
import org.terminal.console.log.Level;

public class Ariia {

	public static void main(String[] args) {

		Argument arguments = null;
		ChromeConnection chrome = null;
		if (ChromeConnection.iSChromStream(args)) {

			chrome = new ChromeConnection();
			chrome.redChromeMessage(/* 629 */);
			arguments = chrome.getArguments();
			// chrome.send(arguments.getDictionary());

		} else {
			arguments = new Argument(args);
		}

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
