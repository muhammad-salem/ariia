package org.okaria.okhttp.service;

import java.io.IOException;

import org.okaria.okhttp.client.Client;
import org.terminal.console.log.Log;

import okhttp3.Response;

public class NetworkConnectivity {

	
	/*
	 * GET / HTTP/1.1
	 * Host: connectivity-check.ubuntu.com
	 */
	/*
	 * HTTP/1.1 204 No Content
	 * Date: Thu, 26 Mar 2020 09:44:44 GMT
	 * Server: Apache/2.4.18 (Ubuntu)
	 * X-NetworkManager-Status: online
	 * Keep-Alive: timeout=5, max=100
	 * Connection: Keep-Alive
	 */
	// http://connectivity-check.ubuntu.com/
	private final String CONNECTIVITY_URL = "http://connectivity-check.ubuntu.com/"; 
	private Client client;
	public NetworkConnectivity(Client client) {
		this.client = client;
	}

	
	public boolean isOnline() {
		try {
			Response response = client.get(CONNECTIVITY_URL);
			Log.trace(getClass(),
					"Ubunt Server - Connectivity Check",
					"X-NetworkManager-Status: " + response.header("X-NetworkManager-Status"));
//			Log.trace(getClass(), "Ubunt Server - Connectivity Check", response.headers().toString());
			return true;
		} catch (IOException e) {
			Log.debug(getClass(), CONNECTIVITY_URL, e.getMessage());
			return false;
		}
	}
	

}
