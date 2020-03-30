package org.okaria;

import java.util.Arrays;

import org.okaria.chrome.ChromeConnection;
import org.okaria.lunch.Argument;
import org.okaria.lunch.ItemBuilder;
import org.okaria.lunch.TerminalArgument;
import org.okaria.okhttp.service.ServiceManager;
import org.okaria.setting.Properties;
import org.okaria.util.R;
import org.terminal.console.log.Level;
import org.terminal.console.log.Log;

public class OKAria {

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
		Log.trace(OKAria.class, "user input", Arrays.toString(args));
		Properties.Config(arguments);

		ServiceManager manager = ServiceManager.SegmentServiceManager(arguments.getProxy());
		manager.startScheduledService();
		
		Log.trace(OKAria.class, "Set Shutdown Hook Thread", "register shutdown thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			manager.runSystemShutdownHook();
			manager.printReport();
			System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
		}));
		
		ItemBuilder builder = new ItemBuilder(arguments);
		
		manager.initForDownload(builder.getItems());
		manager.setFinishAction(() ->  System.exit(0));

	}

}
