package org.ariia.javafx.controllers;

import javafx.beans.property.*;
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

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty download = new SimpleStringProperty();
    private StringProperty progress = new SimpleStringProperty();
    private StringProperty length = new SimpleStringProperty();
    private StringProperty remaining = new SimpleStringProperty();
    private StringProperty speed = new SimpleStringProperty();
    private StringProperty timeLeft = new SimpleStringProperty();

    public void updateMonitoring(){
        id.setValue(item.getId());
        name.set(item.getFilename());
        url.set(item.getUrl());
        status.set(item.getState().toString());
        length.set(monitor.getFileLength());
        progress.set(monitor.getPercent());
        download.set(monitor.getDownloadLength());
        remaining.set(monitor.getRemainingLength());
        speed.set(monitor.getTcpDownloadSpeed() + "/s");
        timeLeft.set(monitor.getRemainingTimeString());
    }

}
