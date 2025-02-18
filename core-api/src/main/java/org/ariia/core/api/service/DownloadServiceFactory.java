package org.ariia.core.api.service;


import org.ariia.core.api.client.Client;
import org.ariia.monitors.SessionReport;
import org.network.connectivity.ConnectivityCheck;
import org.network.connectivity.UrlConnectivity;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DownloadServiceFactory {

    public final static int SCHEDULE_POOL = 10;

    protected ScheduledExecutorService scheduledService;
    protected Client client;
    protected SessionReport sessionReportFactory;
    protected ConnectivityCheck connectivityCheck;
    protected boolean useSessionReportFactory = false;

    public DownloadServiceFactory(Client client) {
        this.client = client;
    }

    public DownloadServiceBuilder builder() {
        DownloadServiceBuilder builder = new DownloadServiceBuilder();
        builder.setClient(client);
        if (useSessionReportFactory) {
            builder.setSessionReport(sessionReportFactory);
        }
        if (Objects.isNull(scheduledService)) {
            this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL, Thread.ofVirtual().factory());
        }
        builder.setScheduledService(scheduledService);
        if (Objects.isNull(connectivityCheck)) {
            this.connectivityCheck = new UrlConnectivity(client.getProxy());
        }
        builder.setConnectivityCheck(connectivityCheck);
        return builder;
    }

    public Client getClient() {
        return client;
    }

    public ScheduledExecutorService getScheduledService() {
        return scheduledService;
    }

    public void setScheduledService(ScheduledExecutorService scheduledService) {
        this.scheduledService = scheduledService;
    }

    public SessionReport getSessionReportFactory() {
        return sessionReportFactory;
    }

    public void setSessionReportFactory(SessionReport sessionReportFactory) {
        this.sessionReportFactory = sessionReportFactory;
        this.useSessionReportFactory = true;
    }

    public ConnectivityCheck getConnectivityCheck() {
        return connectivityCheck;
    }

    public void setConnectivityCheck(ConnectivityCheck connectivityCheck) {
        this.connectivityCheck = connectivityCheck;
    }
}
