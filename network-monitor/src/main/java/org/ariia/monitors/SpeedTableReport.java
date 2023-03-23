package org.ariia.monitors;

public interface SpeedTableReport {

    boolean add(RangeReport monitor);

    void remove(RangeReport monitor);

    void clear();

    String getTableReport();

    void updateOneCycle();

    SessionReport getSessionMonitor();

}