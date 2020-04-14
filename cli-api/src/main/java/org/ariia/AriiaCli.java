package org.ariia;

import java.util.Arrays;
import java.util.function.Function;

import org.ariia.args.Argument;
import org.ariia.args.TerminalArgument;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.ItemBuilder;
import org.ariia.logging.Log;
import org.ariia.util.R;
import org.terminal.console.log.Level;

public class AriiaCli {
	
	private ServiceManager manager;
	private Function<Void, Client> clientIfNeeded;
	private Client client;
	private Runnable finishAction = () -> {};
	
	public AriiaCli(Function<Void, Client> clientIfNeeded) {
		this.clientIfNeeded = clientIfNeeded;
	}
	
	public void setClientIfNeeded(Function<Void, Client> clientIfNeeded) {
		this.clientIfNeeded = clientIfNeeded;
	}
	
	public Function<Void, Client> getClientIfNeeded() {
		return clientIfNeeded;
	}
	
	public ServiceManager getManager() {
		return manager;
	}
	
	public Runnable getFinishAction() {
		return finishAction;
	}
	
	public void setFinishAction(Runnable finishAction) {
		this.finishAction = finishAction;
	}
	
	public void lunch(String[] args) {
		lunch(new Argument(args));
	}

	public void lunch(Argument arguments ) {
		
		
		R.MK_DIRS(R.ConfigPath);

		String log_level = 
				arguments.getOrDefault(TerminalArgument.Debug, Level.info.name());
		Log.level(log_level);
		Log.trace(AriiaCli.class, "Terminal Argument", Arrays.toString(arguments.getArgs()));
		Properties.Config(arguments);

		client = clientIfNeeded.apply(null);
		manager = new ServiceManager(client);
		
		Log.trace(AriiaCli.class, "Set Shutdown Hook Thread", "register shutdown thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			manager.close();
			manager.runSystemShutdownHook();
			manager.printAllReport();
			System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
		}));
		
		ItemBuilder builder = new ItemBuilder(arguments);
		
		manager.initForDownload(builder.getItems());
		manager.setFinishAction(finishAction);
		manager.startScheduledService();
	}

}
