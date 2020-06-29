package org.ariia.network;

public class NetworkReport {

    private String title;
    private String message;
    private NetworkStatus networkStatus;

    public NetworkReport(NetworkStatus networkStatus, String title, String message) {
        this.networkStatus = networkStatus;
        this.title = title;
        this.message = message;
    }

    public NetworkReport(NetworkStatus networkStatus, String message) {
        this.networkStatus = networkStatus;
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

    public boolean isConnected() {
        return NetworkStatus.Connected.equals(networkStatus);
    }

    public boolean isRedirected() {
        return NetworkStatus.Redirected.equals(networkStatus);
    }

    public boolean isDisconnected() {
        return NetworkStatus.Disconnected.equals(networkStatus);
    }

}
