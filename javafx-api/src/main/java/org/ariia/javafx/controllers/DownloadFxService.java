package org.ariia.javafx.controllers;

import org.ariia.core.api.service.DownloadService;
import org.ariia.core.api.writer.ItemMetaData;

import java.util.concurrent.SubmissionPublisher;

public class DownloadFxService extends DownloadService {

    private SubmissionPublisher<ItemMetaData> updatePublisher = new SubmissionPublisher();
    private SubmissionPublisher<ItemMetaData> addPublisher = new SubmissionPublisher();
    private SubmissionPublisher<ItemMetaData> removePublisher = new SubmissionPublisher();

    public DownloadFxService() {
        super();
    }
    @Override
    public void startScheduledService() {
        itemDataStore.getAll().forEach(this::download);
        super.startScheduledService();
    }

    @Override
    protected void waitEvent(ItemMetaData item) {
        super.waitEvent(item);
        updatePublisher.submit(item);
    }

    @Override
    protected void downloadEvent(ItemMetaData item) {
        super.downloadEvent(item);
        updatePublisher.submit(item);
    }

    @Override
    protected void pauseEvent(ItemMetaData item) {
        super.pauseEvent(item);
        updatePublisher.submit(item);
    }

    @Override
    protected void completeEvent(ItemMetaData item) {
        super.completeEvent(item);
        updatePublisher.submit(item);
    }

    @Override
    public void addItemMetaData(ItemMetaData item) {
        super.addItemMetaData(item);
        addPublisher.submit(item);
    }

    @Override
    public void removeItemMetaData(ItemMetaData item) {
        super.removeItemMetaData(item);
        updatePublisher.submit(item);
    }

    @Override
    public void printReport() {
        this.speedTableReport.updateOneCycle();
        this.itemStream().forEach(updatePublisher::submit);
    }

    public SubmissionPublisher<ItemMetaData> getUpdatePublisher() {
        return updatePublisher;
    }

    public SubmissionPublisher<ItemMetaData> getAddPublisher() {
        return addPublisher;
    }

    public SubmissionPublisher<ItemMetaData> getRemovePublisher() {
        return removePublisher;
    }

}
