package org.ariia.javafx.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ariia.items.Item;
import org.ariia.monitors.RangeReport;

@Getter
@RequiredArgsConstructor
public class ItemProperty {

    public static ItemProperty of(Item item, RangeReport monitor) {
        return new ItemProperty(item, monitor);
    }

    private final Item item;
    private final RangeReport monitor;
    private StringProperty name = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty download = new SimpleStringProperty();
    private StringProperty progress = new SimpleStringProperty();
    private StringProperty length = new SimpleStringProperty();
    private StringProperty remaining = new SimpleStringProperty();
    private StringProperty timeLeft = new SimpleStringProperty();

    public void updateMonitoring(){
        name.set(item.getFilename());
        url.set(item.getUrl());
        length.set(monitor.getFileLength());
        progress.set(monitor.getPercent());
        download.set(monitor.getDownloadLength());
        remaining.set(monitor.getRemainingLength());
        timeLeft.set(monitor.getRemainingTimeString());
        if (monitor.isDownloading()){
            status.set(monitor.getTcpDownloadSpeed() + "/s");
        } else {
            status.set(item.getState().toString());
        }
    }

}
