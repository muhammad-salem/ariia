package org.ariia.core.api.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ariia.config.Properties;
import org.ariia.core.api.client.Client;
import org.ariia.core.api.writer.ChannelMetaDataWriter;
import org.ariia.core.api.writer.ItemMetaData;
import org.ariia.core.api.writer.ItemMetaDataCompleteWrapper;
import org.ariia.core.api.writer.StreamMetaDataWriter;
import org.ariia.items.DataStore;
import org.ariia.items.Item;
import org.ariia.items.ItemState;
import org.ariia.logging.Log;
import org.ariia.logging.Logger;
import org.ariia.monitors.SessionReport;
import org.ariia.monitors.SpeedTableReport;
import org.network.connectivity.ConnectivityCheck;
import org.network.connectivity.NetworkReport;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * item life cycle metadata to the waiting list then it moved to download a list if
 * finish >> to complete the list else to pause list and if any action to waiting
 * list again <br/>
 * item --> [item metadata] --> add to [waiting list] -- > [download list] -->
 * [ complete list] |---<--- [pause list] <--|
 */
@Getter @Setter
@NoArgsConstructor
public class DownloadService implements Closeable {

    public final static int SCHEDULE_TIME = 1;
    private static final Logger log = Logger.create(DownloadService.class);
    // public final static int SCHEDULE_POOL = 10;
    protected ScheduledExecutorService scheduledService;

    protected List<ItemMetaData> itemMetaDataList;
    protected DataStore<Item> itemDataStore;
    protected Client defaultClient;
    protected SessionReport sessionReport;
    protected SpeedTableReport speedTableReport;
    protected ConnectivityCheck connectivityCheck;

    protected Properties properties;
    protected Runnable finishAction = () -> {};

    protected boolean allowDownload = true;
    protected boolean allowPause = false;
    protected boolean allowPrintingReport = true;

    private final SubmissionPublisher<ItemMetaData> updatePublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<ItemMetaData> addPublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<ItemMetaData> removePublisher = new SubmissionPublisher<>();
    private final SubmissionPublisher<Integer> cyclePublisher = new SubmissionPublisher<>();

    private void runEvent(SubmissionPublisher<ItemMetaData> publisher, ItemMetaData metaData) {
        try {
            publisher.submit(metaData);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void waitEvent(ItemMetaData metaData) {
        runEvent(updatePublisher, metaData);
    }

    private void pauseEvent(ItemMetaData metaData) {
        runEvent(updatePublisher, metaData);
    }

    private void downloadEvent(ItemMetaData metaData) {
        runEvent(updatePublisher, metaData);
    }

    private void completeEvent(ItemMetaData metaData) {
        runEvent(updatePublisher, metaData);
    }

    public void addItemMetaData(ItemMetaData metaData) {
        metaData.initWaitQueue();
        moveToWaitingList(metaData);
        itemMetaDataList.add(metaData);
        sessionReport.addRange(metaData.getRangeInfo());
        runEvent(addPublisher, metaData);
    }

    public void removeItemMetaData(ItemMetaData metaData) {
        itemMetaDataList.remove(metaData);
        sessionReport.removeRange(metaData.getRangeInfo());
        runEvent(removePublisher, metaData);
        // speedTableReport.remove(metaData.getRangeReport());
    }

    private void moveToWaitingList(ItemMetaData metaData) {
        if (metaData.getItem().getState().canMoveToWaitState()) {
            metaData.getItem().setState(ItemState.WAITING);
            metaData.getRangeInfo().oneCycleDataUpdate();
            speedTableReport.remove(metaData.getRangeReport());
            log.log("Waiting", metaData.getItem().getFilename(), metaData.getItem().toString());
            waitEvent(metaData);
        }
    }

    public void moveToDownloadList(ItemMetaData metaData) {
        if (metaData.getItem().getState().isWaiting()) {
            metaData.getItem().setState(ItemState.DOWNLOADING);
            metaData.initWaitQueue();
            metaData.checkCompleted();
            metaData.getRangeInfo().oneCycleDataUpdate();
            speedTableReport.add(metaData.getRangeReport());
            metaData.startAndCheckDownloadQueue(sessionReport.getMonitor());
            itemDataStore.save(metaData.getItem());
            log.log("Download", metaData.getItem().getFilename(), metaData.getItem().toString());
            downloadEvent(metaData);
        }
    }

    private void moveToCompleteList(ItemMetaData metaData) {
        if (metaData.getItem().getState().isDownloading()) {
            metaData.systemFlush();
            metaData.getItem().setState(ItemState.COMPLETE);
            itemDataStore.save(metaData.getItem());
            speedTableReport.remove(metaData.getRangeReport());
            metaData.close();
            log.log("Complete", metaData.getItem().getFilename(), metaData.getItem().liteString());
            completeEvent(metaData);
        }
    }

    public void moveToPauseList(ItemMetaData metaData) {
        if (allowPause) {
            // if (metaData.getItem().getState().isDownloading()) {
            metaData.pause();
            speedTableReport.remove(metaData.getRangeReport());
            metaData.getItem().setState(ItemState.PAUSE);
            itemDataStore.save(metaData.getItem());
            log.log("Pause", metaData.getItem().getFilename(), metaData.getItem().toString());
            pauseEvent(metaData);
            // }
        } else {
            metaData.pause();
            moveToWaitingList(metaData);
        }

    }

    public void deleteFromList(ItemMetaData metaData) {
        removeItemMetaData(metaData);
        metaData.getItem().setState(ItemState.DELETE);
        metaData.pause();
        metaData.systemFlush();
        itemDataStore.remove(metaData.getItem());
    }

    public void startScheduledService() {
        itemDataStore.getAll().forEach(this::download);
        scheduledService.scheduleWithFixedDelay(this::checkDownloadList, 1, SCHEDULE_TIME, TimeUnit.SECONDS);
        scheduledService.scheduleWithFixedDelay(this::printReport, 2, 1, TimeUnit.SECONDS);
        scheduledService.scheduleWithFixedDelay(this::systemFlushData, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        scheduledService.shutdownNow();
        defaultClient.shutdownServiceNow();
    }

    public Stream<ItemMetaData> itemStream() {
        return itemMetaDataList.stream();
    }

    public Stream<ItemMetaData> downloadStream() {
        return itemMetaDataList.stream().filter(metaData -> metaData.getItem().getState().isDownloading());
    }

    private Stream<ItemMetaData> waitStream() {
        return itemMetaDataList.stream().filter(metaData -> metaData.getItem().getState().isWaiting());
    }

    protected Stream<ItemMetaData> pauseStream() {
        return itemMetaDataList.stream().filter(metaData -> metaData.getItem().getState().isPause());
    }

    protected Stream<ItemMetaData> completeStream() {
        return itemMetaDataList.stream().filter(metaData -> metaData.getItem().getState().isComplete());
    }

    public int getDownloadCount() {
        return (int) downloadStream().count();
    }

    public boolean isFinishTime() {
        return waitStream().findAny().isEmpty() & downloadStream().findAny().isEmpty() & pauseStream().findAny().isEmpty();
    }

    protected boolean isItemListHadItemsToDownload() {
        return itemMetaDataList.stream().anyMatch(metaData -> {
            var state = metaData.getItem().getState();
            return state.isDownloading() || state.isWaiting();
        });
    }

    public boolean isItRequiredToPauseDownloadList() {
        if (sessionReport.isDownloading()) {
            return false;
            // } else if (isItemListHadItemsToDownload()) {
            // return false;
        } else {
            NetworkReport report = connectivityCheck.networkReport();
            Log.trace(connectivityCheck.getClass(), report.getTitle(), report.getMessage());
            return !report.isConnected();
        }
    }

    protected void checkDownloadList() {
        log.trace("DownloadService.checkDownloadList");
        // if (!allowDownload) { return;}
        // if (!isItemListHadItemsToDownload()) { return;}

        if (!allowDownload || itemMetaDataList.isEmpty()) {
            log.trace("Check Pause Download Service", "Allow Download: false");
            // check download to pause
            downloadStream().forEach(this::moveToWaitingList);
            return;
        }

        if (isItRequiredToPauseDownloadList()) {
            log.trace("Check Network Connection", "Network Connectivity Statues: NETWORK DISCONNECTED");
            // check download to pause
            downloadStream().forEach(this::moveToWaitingList);
        } else {
            // check download to complete
            downloadStream().forEach(metaData -> {
                if (metaData.getRangeInfo().isFinish()) {
                    moveToCompleteList(metaData);
                } else {
                    metaData.checkWhileDownloading();
                    metaData.startAndCheckDownloadQueue(sessionReport.getMonitor());
                }
            });
            // check waiting to download
            long reamingActiveDownloadPool = properties.getMaxActiveDownloadPool() - getDownloadCount();
            if (reamingActiveDownloadPool > 0) {
                waitStream().limit(reamingActiveDownloadPool).forEach(this::moveToDownloadList);
            }
            if (isFinishTime()) {
                finishAction.run();
            }
        }

    }

    public void runSystemShutdownHook() {
        for (var metaData : itemMetaDataList) {
            metaData.systemFlush();
            if (metaData.getItem().getState().isDownloading()) {
                metaData.getItem().setState(ItemState.PAUSE);
            }
            itemDataStore.save(metaData.getItem());
            metaData.close();
        }
    }

    public void printReport() {
        if (allowPrintingReport) {
            System.out.println(speedTableReport.getTableReport());
        } else {
            speedTableReport.updateOneCycle();
        }
        this.itemStream().forEach(updatePublisher::submit);
        try {
            cyclePublisher.submit(1);
        } catch (Exception e) {
            log.error("Error notify cycle update");
        }
    }

    protected void systemFlushData() {
        downloadStream().forEach(metaData -> {
            metaData.systemFlush();
            itemDataStore.save(metaData.getItem());
        });
    }

    public final ItemMetaData initializeItemMetaData(Item item) {
        return initializeItemMetaData(item, null);
    }

    public final ItemMetaData initializeItemMetaData(Item item, Client client) {
        var itemClient = Objects.isNull(client) ? this.defaultClient : client;
        if (item.getRangeInfo().isFinish()) {
            return new ItemMetaDataCompleteWrapper(item, itemClient, properties);
        } else if (item.getRangeInfo().isStreaming()) {
            return new StreamMetaDataWriter(item, itemClient, properties);
        } else {
            return new ChannelMetaDataWriter(item, itemClient, properties);
        }
        // else if(Integer.MAX_VALUE > range.getFileLength()) {
        // return new SimpleMappedMetaDataWriter(item, itemClient, properties);
        // } else {
        // return new LargeMappedMetaDataWriter(item, itemClient, properties);
        // }
    }

    public void download(Item item) {
        var metaData = initializeItemMetaData(item);
        addItemMetaData(metaData);

        // if (item.getRangeInfo().isFinish() ||
        // metaData.getItem().getState().isComplete()) {
        // moveToCompleteList(metaData);
        // } else if (metaData.getItem().getState().isPause()) {SIZEOF_EPOLLEVENT
        // moveToPauseList(metaData);
        // } else if (metaData.getItem().getState().isDownloading()) {
        // moveToDownloadList(metaData);
        // } else {
        // item.setState(ItemState.INIT_FILE);
        // moveToWaitingList(metaData);
        // }
        log.trace(item.getFilename(), "Meta Data Writer: " + metaData.getClass().getSimpleName());
        log.log("add download item to list", item.toString());
    }

    public void initializeItemOnlineAndDownload(Item item) {
        scheduledService.execute(() -> {
            defaultClient.updateItemOnline(item);
            download(item);
        });
    }

    public void fetchUrlInfo(Item item) {
        defaultClient.updateItemOnline(item);
    }

    public void initializeFromDataStore(List<Item> items) {
        for (var item : items) {
            var old = itemDataStore.findByUrlAndSaveDirectory(item.getUrl(), item.getSaveDirectory());
            if (Objects.isNull(old)) {
                defaultClient.updateItemOnline(item);
            } else {
                old.getRangeInfo().checkRanges();
                old.addHeaders(item.getHeaders());
                item.copy(old);
            }
            download(item);
        }
    }

    public Optional<ItemMetaData> searchById(Integer id) {
        return itemMetaDataList.stream()
                .filter(item -> Objects.equals(item.getItem().getId(), id))
                .findAny();
    }

    public Properties getProperties() {
        return defaultClient.getProperties();
    }

}
