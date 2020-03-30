package org.okaria.okhttp.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.okaria.network.CheckServer;
import org.okaria.network.ConnectivityCheck;
import org.okaria.network.NetworkStatus;
import org.okaria.okhttp.client.Client;
import org.terminal.console.log.Log;

import okhttp3.Response;

public class NetworkConnectivity implements ConnectivityCheck {
	
	private  Queue<CheckServer> servers = new LinkedList<>();
	
	
	private Client client;
	
	public NetworkConnectivity(Client client) {
		this.client = client;
		this.servers.addAll(Arrays.asList(CheckServer.values()));
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public boolean isOnline() {
		CheckServer server = servers.poll();
		try {
			// if we get any response 
			Response response = client.get(server.url());
			Log.trace(getClass(), server.serverName(), response.headers().toString());
			return true;
		} catch (IOException e) {
			Log.debug(getClass(), server.serverName(), e.getMessage());
			return false;
		} finally {
			servers.offer(server);
		}
	}
	
	public NetworkStatus getNetworkStatus() {
		CheckServer server = servers.poll();
		try {
			// if we get any response 
			Response response = client.get(server.url());
			Log.trace(getClass(), server.serverName(), response.headers().toString());
			if (response.isRedirect()) {
				return NetworkStatus.Redirected;
			}
			return NetworkStatus.Connected;
		} catch (IOException e) {
			Log.debug(getClass(), server.serverName(), e.getMessage());
			return NetworkStatus.Disconnected;
		} finally {
			servers.offer(server);
		}
	}

}
