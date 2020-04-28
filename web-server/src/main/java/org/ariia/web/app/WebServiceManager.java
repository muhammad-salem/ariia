package org.ariia.web.app;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.service.ServiceManager;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.monitors.SimpleSessionMonitor;
import org.ariia.monitors.TableMonitor;
import org.ariia.network.ConnectivityCheck;

public class WebServiceManager extends ServiceManager {
	
	public WebServiceManager(Client client) {
		super(client);
		
	}
	
	public WebServiceManager(Client client, DataStore<Item> dataStore) {
		super(client, dataStore);
	}
	
	public WebServiceManager(Client client, TableMonitor reportTable) {
		super(client, reportTable);
	}
	
	public WebServiceManager(Client client, SimpleSessionMonitor monitor,
			ConnectivityCheck connectivity, TableMonitor reportTable)
	{
		super(client, monitor, connectivity, reportTable);
	}

}
