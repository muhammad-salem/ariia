package org.ariia.cli;

import java.util.Objects;
import java.util.function.Function;

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
	private Function<Void, Client> clientResolver;
	private Function<Void, ServiceManager> serviceManagerResolver;
	private Runnable finishAction = () -> {};
	
	public AriiaCli(Function<Void, Client> clientResolver) {
		this.clientResolver = Objects.requireNonNull(clientResolver);
	}
	
	public AriiaCli(Function<Void, ServiceManager> serviceManagerResolver, Void v) {
		this.serviceManagerResolver = Objects.requireNonNull(serviceManagerResolver);
	}
	
	public AriiaCli(Function<Void, Client> clientResolver, Runnable finishAction) {
		this.clientResolver = Objects.requireNonNull(clientResolver);
		this.finishAction = Objects.requireNonNull(finishAction);
	}
	
	public AriiaCli(Function<Void, ServiceManager> serviceManagerResolver, Runnable finishAction, Void v) {
		this.serviceManagerResolver = Objects.requireNonNull(serviceManagerResolver);
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
		
		if (Objects.isNull(serviceManagerResolver)) {
			client = clientResolver.apply(null);
			serviceManager = new ServiceManager(client);
		} else {
			serviceManager = serviceManagerResolver.apply(null);
			client = serviceManager.getClient();
		}
		
		Log.trace(AriiaCli.class, "Set Shutdown Hook Thread", "register shutdown thread");
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
