package org.ariia.network;

public class NetworkReport {
	
	private String title;
	private String message;
	private NetworkStatus networkStatus;
	
	public NetworkReport(NetworkStatus networkStatus, String title, String message) {
		this.networkStatus =networkStatus;
		this.title = title;
		this.message = message;
	}
	
	public NetworkReport(NetworkStatus networkStatus, String message) {
		this.networkStatus =networkStatus;
		this.message = message;
	}
	
	public NetworkReport(String title, String message) {
		this.title = title;
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getTitle() {
		return title;
	}
		
	public NetworkStatus getNetworkStatus() {
		return networkStatus;
	}

}
