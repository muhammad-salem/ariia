package org.ariia.cli;

import java.util.Objects;

import org.ariia.args.Argument;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.ItemBuilder;
import org.ariia.logging.Log;
import org.ariia.util.R;

public class AriiaCli {

	private Client client;
	private ServiceManager serviceManager;
	private Runnable finishAction = () -> {};
	
	public AriiaCli(Client client) {
		this.client = Objects.requireNonNull(client);
	}
	
	public AriiaCli(ServiceManager serviceManager ) {
		this.serviceManager = Objects.requireNonNull(serviceManager);
	}
	
	public AriiaCli(Client client, Runnable finishAction) {
		this.client = Objects.requireNonNull(client);
		this.finishAction = Objects.requireNonNull(finishAction);
	}
	
	public AriiaCli(ServiceManager serviceManager, Runnable finishAction, Void v) {
		this.serviceManager = Objects.requireNonNull(serviceManager);
		this.finishAction = Objects.requireNonNull(finishAction);
	}
	
	public Client getClient() {
		return client;
	}
	
	public ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	public Runnable getFinishAction() {
		return finishAction;
	}
	
	public void lunch(String[] args) {
		lunch(new Argument(args));
	}

	public void lunch(Argument arguments ) {
		
		R.MK_DIRS(R.CachePath);
		Properties.Config(arguments);
		
		if (Objects.isNull(serviceManager)) {
			serviceManager = new ServiceManager(client);
		} else {
			client = serviceManager.getClient();
		}
		
		Log.trace(getClass(), "Set Shutdown Hook Thread", "register shutdown thread");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			manager.close();
			serviceManager.runSystemShutdownHook();
			serviceManager.printAllReport();
			System.out.println("\u001B[50B\u001B[0m\nGood Bye!\n");
		}));
		
		ItemBuilder builder = new ItemBuilder(arguments);
		
		serviceManager.initForDownload(builder.getItems());
		builder.clear();
		serviceManager.setFinishAction(finishAction);
		serviceManager.startScheduledService();
	}

}
