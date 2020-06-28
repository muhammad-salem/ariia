package org.ariia.core.api.service;

import org.ariia.core.api.client.Client;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.items.ItemStore;
import org.ariia.monitors.MiniSpeedTableReport;
import org.ariia.monitors.SessionReport;
import org.ariia.monitors.SimpleSessionReport;
import org.ariia.monitors.SpeedTableReport;
import org.ariia.network.ConnectivityCheck;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class DownloadServiceBuilder {

    protected ScheduledExecutorService scheduledService;
    protected ConnectivityCheck connectivityCheck;
    protected Client client;

    protected DataStore<Item> itemDataStore;
    protected List<ItemMetaData> itemMetaDataList = new ArrayList<>();


    protected boolean allowDownload = true;
    protected boolean allowPause = false;

    protected SessionReport sessionReport;
    protected SpeedTableReport speedTableReport;
    private Runnable finishAction = () -> {};


    void setScheduledService(ScheduledExecutorService scheduledService) {
        this.scheduledService = scheduledService;
    }

    void setConnectivityCheck(ConnectivityCheck connectivityCheck) {
        this.connectivityCheck = connectivityCheck;
    }

    void setClient(Client client) {
        this.client = client;
    }

    public void setItemDataStore(DataStore<Item> itemDataStore) {
        this.itemDataStore = itemDataStore;
    }

    public void setSpeedTableReport(SpeedTableReport speedTableReport) {
        this.speedTableReport = speedTableReport;
    }

    public void setFinishAction(Runnable finishAction) {
        this.finishAction = finishAction;
    }

    public void setSessionReport(SessionReport sessionReport) {
        this.sessionReport = sessionReport;
    }

    public void useCliApp() {
        this.allowPause = false;
        this.allowDownload = true;
    }

    public void useWebApp() {
        this.allowPause = true;
        this.allowDownload = false;
    }

    public DownloadService build() {
        return build(new DownloadService());
    }

    public DownloadService build(DownloadService downloadService) {
        downloadService.itemMetaDataList = this.itemMetaDataList;
        downloadService.allowPause = this.allowPause;
        downloadService.allowDownload = this.allowDownload;

        downloadService.scheduledService = this.scheduledService;
        downloadService.connectivityCheck = this.connectivityCheck;
        downloadService.defaultClient = this.client;
        downloadService.properties = this.client.getProperties();

        if (Objects.isNull(speedTableReport)) {
            if (Objects.isNull(sessionReport)){
                sessionReport = new SimpleSessionReport();
            }
            speedTableReport = new MiniSpeedTableReport(sessionReport);
        }
        downloadService.speedTableReport = speedTableReport;
        downloadService.sessionReport = speedTableReport.getSessionMonitor();

        if (Objects.isNull(itemDataStore)) {
            itemDataStore = new ItemStore();
        }
        downloadService.itemDataStore =itemDataStore;

        if (Objects.nonNull(finishAction)){
            downloadService.finishAction = finishAction;
        }
        return downloadService;
    }

    public DataStore<Item> getItemDataStore() {
        return itemDataStore;
    }

    public SpeedTableReport getSpeedTableReport() {
        return speedTableReport;
    }

    public SessionReport getSessionReport() {
        return sessionReport;
    }

    public Runnable getFinishAction() {
        return finishAction;
    }

    public ScheduledExecutorService getScheduledService() {
        return scheduledService;
    }

    public ConnectivityCheck getConnectivityCheck() {
        return connectivityCheck;
    }

    public Client getClient() {
        return client;
    }
}
