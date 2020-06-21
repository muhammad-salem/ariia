package org.ariia.core.api.service;

import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.writer.ChannelMetaDataWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.ItemMetaDataCompleteWrapper;
import org.ariia.core.api.writer.StreamMetaDataWriter;
import org.ariia.items.*;
import org.ariia.logging.Log;
import org.ariia.monitors.MiniSpeedTableReport;
import org.ariia.monitors.SessionReport;
import org.ariia.monitors.SimpleSessionReport;
import org.ariia.monitors.SpeedTableReport;
import org.ariia.network.ConnectivityCheck;
import org.ariia.network.NetworkReport;
import org.ariia.network.UrlConnectivity;
import org.ariia.range.RangeUtil;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServiceManager implements Closeable {

    public final static int SCHEDULE_TIME = 1;
    public final static int SCHEDULE_POOL = 10;

    protected ScheduledExecutorService scheduledService;

    protected Queue<ItemMetaData> waitingList;
    protected Queue<ItemMetaData> pauseList;
    protected Queue<ItemMetaData> downloadingList;
    protected Queue<ItemMetaData> completingList;

    protected DataStore<Item> dataStore;
    protected Client client;
    protected SessionReport sessionReport;
    protected SpeedTableReport reportTable;
    protected ConnectivityCheck connectivity;

    protected Properties properties;

    private Runnable finishAction = () -> {
    };

    public ServiceManager(Client client) {
        this.client = client;
        this.sessionReport = new SimpleSessionReport();
        this.reportTable = new MiniSpeedTableReport(sessionReport);
        this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
        this.connectivity = new UrlConnectivity(client.getProxy());
        this.initServiceList(new ItemStore());
    }

    public ServiceManager(Client client, DataStore<Item> dataStore) {
        this.client = client;
        this.sessionReport = new SimpleSessionReport();
        this.reportTable = new MiniSpeedTableReport(sessionReport);
        this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
        this.connectivity = new UrlConnectivity(client.getProxy());
        this.initServiceList(dataStore);
    }

    public ServiceManager(Client client, SpeedTableReport reportTable) {
        this.client = client;
        this.reportTable = reportTable;
        this.sessionReport = reportTable.getSessionMonitor();
        this.connectivity = new UrlConnectivity(client.getProxy());

        this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
        this.initServiceList(new ItemStore());
    }

    public ServiceManager(Client client, SessionReport monitor, SpeedTableReport reportTable) {
        this(client, monitor, new UrlConnectivity(client.getProxy()), reportTable);
    }

    public ServiceManager(Client client, SessionReport monitor,
                          ConnectivityCheck connectivity, SpeedTableReport reportTable) {
        this.client = client;
        this.sessionReport = monitor;
        this.reportTable = reportTable;
        this.connectivity = connectivity;

        this.scheduledService = Executors.newScheduledThreadPool(SCHEDULE_POOL);
        this.initServiceList(new ItemStore());
    }

    protected void initServiceList(DataStore<Item> dataStore) {
        this.dataStore = dataStore;
        this.waitingList = new LinkedList<>();
        this.pauseList = new LinkedList<>();
        this.downloadingList = new LinkedList<>();
        this.completingList = new LinkedList<>();
        this.properties = client.getProperties();
    }


    //	@Override
    public void startScheduledService() {
        scheduledService.scheduleWithFixedDelay(this::checkDownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
        scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
        scheduledService.scheduleWithFixedDelay(this::systemFlushData, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        client.stopService();
        scheduledService.shutdownNow();
    }

    public boolean isNetworkFailed() {
        if (sessionReport.isDownloading()) {
            return false;
        } else if (downloadingList.isEmpty()
                & waitingList.isEmpty()
                & pauseList.isEmpty()) {
            return false;
        } else {
            NetworkReport report = connectivity.networkReport();
            Log.trace(connectivity.getClass(), report.getTitle(), report.getMessage());
            return !report.isConnected();
        }
    }

    protected void checkDownloadList() {
        if (isNetworkFailed()) {
            Log.log(getClass(), "Check Network Connection",
                    "Network Connectivity Statues: NETWORK DISCONNECTED");
            List<ItemMetaData> pause = new LinkedList<>();
            for (ItemMetaData metaData : downloadingList) {
                moveToPauseList(metaData);
                pause.add(metaData);
            }
            pauseEvent(pause);
        } else {
            if (downloadingList.size() < properties.getMaxActiveDownloadPool()) {
                StringBuilder builder = new StringBuilder();
                while (downloadingList.size() < properties.getMaxActiveDownloadPool()
                        && !pauseList.isEmpty()) {
                    ItemMetaData metaData = pauseList.poll();
                    moveToDownloadList(metaData);
                    builder.append(metaData.getItem().getFilename());
                    builder.append("\tRemaining: ");
                    builder.append(metaData.getItem().getRangeInfo().getRemainingLengthMB());
                    builder.append('\n');
                }
                while (downloadingList.size() < properties.getMaxActiveDownloadPool()
                        && !waitingList.isEmpty()) {
                    ItemMetaData metaData = waitingList.poll();
                    moveToDownloadList(metaData);
                    builder.append(metaData.getItem().getFilename());
                    builder.append("\tRemaining: ");
                    builder.append(metaData.getItem().getRangeInfo().getRemainingLengthMB());
                    builder.append('\n');
                }
                if (builder.length() != 0) {
                    builder.delete(builder.length() - 2, builder.length());
                    Log.log(getClass(), "items added to download list", builder.toString());
                    downloadEvent();
                }
            }

            List<ItemMetaData> complete = new LinkedList<>();
            for (ItemMetaData metaData : downloadingList) {
                Item item = metaData.getItem();
                RangeUtil info = item.getRangeInfo();
                if (info.isFinish()) {
                    moveToCompleteList(metaData);
                    complete.add(metaData);
                    Log.log(getClass(), "Download Finish: " + metaData.getItem().getFilename(),
                            metaData.getItem().liteString());
                    continue;
                }
                metaData.checkWhileDownloading();
                metaData.startAndCheckDownloadQueue(client, sessionReport.getMonitor());
            }
            if (!complete.isEmpty()) {
                completeEvent(complete);
            }

            if (downloadingList.isEmpty() & waitingList.isEmpty() & pauseList.isEmpty()) {
                finishAction.run();
            }
        }

    }

    protected void waitEvent(ItemMetaData item) { }
    protected void pauseEvent(List<ItemMetaData> items) { }
    protected void downloadEvent() { }
    protected void completeEvent(List<ItemMetaData> items) { }

    protected void moveToWaitingList(ItemMetaData metaData) {
        metaData.getItem().setState(ItemState.WAITING);
        metaData.getRangeInfo().oneCycleDataUpdate();
        waitingList.add(metaData);
        reportTable.remove(metaData.getRangeReport());
        dataStore.add(metaData.getItem());
        waitEvent(metaData);
    }

    protected void moveToPauseList(ItemMetaData metaData) {
        metaData.pause();
        downloadingList.remove(metaData);
        reportTable.remove(metaData.getRangeReport());
        pauseList.add(metaData);
        metaData.getItem().setState(ItemState.PAUSE);
        dataStore.save(metaData.getItem());
    }

    protected void moveToDownloadList(ItemMetaData metaData) {
        metaData.getItem().setState(ItemState.DOWNLOADING);
        metaData.initWaitQueue();
        metaData.checkCompleted();
        downloadingList.add(metaData);
        metaData.startAndCheckDownloadQueue(client, sessionReport.getMonitor());
        reportTable.add(metaData.getRangeReport());
        metaData.getRangeInfo().oneCycleDataUpdate();
        dataStore.save(metaData.getItem());
    }

    protected void moveToCompleteList(ItemMetaData metaData) {
        metaData.systemFlush();
        metaData.getItem().setState(ItemState.COMPLETE);
        dataStore.save(metaData.getItem());
        metaData.close();
        downloadingList.remove(metaData);
        reportTable.remove(metaData.getRangeReport());
        completingList.add(metaData);
    }

    public void runSystemShutdownHook() {
        for (ItemMetaData metaData : downloadingList) {
            metaData.systemFlush();
            dataStore.save(metaData.getItem());
            metaData.close();
        }

        for (ItemMetaData metaData : pauseList) {
            dataStore.save(metaData.getItem());
            metaData.close();
        }

        for (ItemMetaData metaData : waitingList) {
            dataStore.save(metaData.getItem());
            metaData.close();
        }
    }


    public void setFinishAction(Runnable runnable) {
        this.finishAction = runnable;
    }

    public ScheduledExecutorService getScheduledService() {
        return scheduledService;
    }

    public Queue<ItemMetaData> getWaitingList() {
        return waitingList;
    }

    protected Queue<ItemMetaData> getDownloadingList() {
        return downloadingList;
    }

    public DataStore<Item> getDataStore() {
        return dataStore;
    }

    public Client getClient() {
        return client;
    }

    public SessionReport getSessionReport() {
        return sessionReport;
    }

    public void printReport() {
        System.out.println(reportTable.getTableReport());
    }

    public void printAllReport() {
        for (ItemMetaData itemMetaData : completingList) {
            reportTable.add(itemMetaData.getRangeReport());
        }
        System.out.println(reportTable.getTableReport());
    }

    protected void systemFlushData() {
        for (ItemMetaData metaData : downloadingList) {
            metaData.systemFlush();
            dataStore.save(metaData.getItem());
        }
    }


//    protected void saveDownloadingItemToDisk() {
//        for (ItemMetaData metaData : downloadingList) {
//            dataStore.save(metaData.getItem());
//        }
//    }
//
//    protected void saveWaitingItemToDisk() {
//        for (ItemMetaData metaData : waitingList) {
//            dataStore.save(metaData.getItem());
//        }
//    }



    public final ItemMetaData initItemMetaData(Item item) {
        ItemMetaData metaData;
        if (item.getRangeInfo().isFinish()){
            metaData = new ItemMetaDataCompleteWrapper(item, properties);
        } else if (item.getRangeInfo().isStreaming()) {
            metaData = new StreamMetaDataWriter(item, properties);
        } else {
            metaData = new ChannelMetaDataWriter(item, properties);
        }

//		else if(Integer.MAX_VALUE  > range.getFileLength()) {
//			metaData = new SimpleMappedMetaDataWriter(item, properties);
//		} else {
//			metaData = new LargeMappedMetaDataWriter(item, properties);
//		}
        return  metaData;
    }


    public void download(Item item) {
        RangeUtil range = item.getRangeInfo();
        sessionReport.addRange(range);
        ItemMetaData metaData = initItemMetaData(item);
        if (range.isFinish()) {
            Log.log(getClass(), "Download Finish: " + item.getFilename(), item.liteString());
            moveToCompleteList(metaData);
            item.setState(ItemState.COMPLETE);
            return;
        }

        item.setState(ItemState.INIT_FILE);
        Log.trace(getClass(), item.getFilename(), "Meta Data Writer: " + metaData.getClass().getSimpleName());
        Log.log(getClass(), "add download item to waiting list", item.toString());
        moveToWaitingList(metaData);
    }

    public void download(List<Item> items) {
        items.forEach(this::download);
    }


    public void initForDownload(List<Item> items) {
        items.forEach(item -> {
            Item old = dataStore.findByUrlAndSaveDirectory(item.getUrl(), item.getSaveDirectory());
            if (Objects.isNull(old)) {
                client.updateItemOnline(item);
            } else {
                old.getRangeInfo().checkRanges();
                old.addHeaders(item.getHeaders());
                item.copy(old);
            }
            download(item);
        });
    }

}
