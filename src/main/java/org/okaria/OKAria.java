package org.okaria;

import java.util.Arrays;

import org.fusesource.hawtjni.runtime.Library;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.internal.CLibrary;
import org.log.beans.Level;
import org.log.concurrent.Log;
import org.okaria.chrome.ChromeConnection;
import org.okaria.lunch.Argument;
import org.okaria.lunch.Lunch;
import org.okaria.lunch.TerminalArgument;
import org.okaria.okhttp.service.TableServiceManager;
import org.okaria.setting.Properties;

public class OKAria {

	public static void main(String[] terminals) {

		// System.out.println(System.getProperties());
		// System.out.println(Arrays.toString(terminals));
		// System.out.println(TerminalArgument.Help());

		// String[] testChrome = new String[terminals.length +1];
		// testChrome[terminals.length] = "gaogianbgnmoompbfkmgnefkbehmeijh";
		// for (int i = 0; i < terminals.length; i++) {
		// testChrome[i] = terminals[i];
		// }
		
		Argument arguments = null;
		ChromeConnection chrome = null;
		if (ChromeConnection.iSChromStream(terminals)) {

			chrome = new ChromeConnection();
			chrome.redChromeMessage(/* 629 */);
			arguments = chrome.getArguments();
			// chrome.send(arguments.getDictionary());

		} else {
			arguments = new Argument(terminals);
		}

		if (arguments.isHelp()) {
			System.out.println(TerminalArgument.Help());
			return;
		} else if (arguments.isVersion()) {
			System.out.println("okaria version \"0.2.23\"");
			return;
		}
		Library lib = new Library("jansi", CLibrary.class);
		lib.load();
		AnsiConsole.systemInstall();

		String log_level = arguments.getOrDefault(TerminalArgument.Debug,
				Level.info.name());
		Log.level(Level.valueOf(log_level));
		Log.fine(OKAria.class, "terminal arguments",
				Arrays.toString(terminals));

		Properties.Config(arguments);

		TableServiceManager manager = TableServiceManager.SegmentServiceManager(arguments.getProxy());
		manager.startScheduledService();
		Lunch lunch = new Lunch(manager);
		lunch.download(arguments);
		manager.setEmptyQueueRunnable(()->{System.exit(0);});
		
		
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			manager.getSystemShutdownHook();
			manager.close();
			System.out.println("\u001B[50B\u001B[0m");
		}));
		

	}

}
