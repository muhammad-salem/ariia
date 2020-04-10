package org.ariia.network;

public interface ConnectivityCheck {

	
	boolean isOnline();

	/**
	 * Perform an HTTP GET request to check network connectivity
	 * if we got any response (code > 200)  --> connected <br>
	 * if ISP redirect the request			--> redirected <br>
	 * if got any network / IO exception 	--> disconnected <br>
	 * @return
	 */
	NetworkStatus networkStatus();
	
	NetworkReport networkReport();

}