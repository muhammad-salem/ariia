package org.okaria;

import java.util.Arrays;

import org.okaria.chrome.ChromeConnection;
import org.terminal.console.log.Log;
import org.okaria.lunch.Argument;
import org.okaria.lunch.Lunch;
import org.okaria.lunch.TerminalArgument;
import org.okaria.okhttp.service.MiniTableServiceManager;
import org.okaria.setting.Properties;
import org.okaria.util.R;
import org.terminal.console.log.Level;

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
		Log.log(OKAria.class, "user input", Arrays.toString(args));
		Properties.Config(arguments);

		MiniTableServiceManager manager = MiniTableServiceManager.SegmentServiceManager(arguments.getProxy());
		manager.startScheduledService();
		Log.trace(OKAria.class, "Set Shutdown Hook Thread", "register shutdown thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			manager.runSystemShutdownHook();
			manager.printReport();
			System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
		}));
		
		Lunch lunch = new Lunch(manager);
		lunch.download(arguments);
		manager.setFinishDownloadQueueEvent(() ->  System.exit(0));

	}

}
