package org.ariia.monitors;

import org.ariia.range.RangeUtil;
import org.ariia.util.Utils;
import org.network.speed.report.SpeedSnapshot;
import org.network.speed.report.TotalSpeedMonitor;
import org.network.speed.report.TotalSpeedReport;
import org.terminal.ansi.CursorControl;
import org.terminal.ansi.Styles;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class SessionReport extends TotalSpeedReport<TotalSpeedMonitor> implements SpeedSnapshot, Styles, CursorControl {

    protected transient MessageFormat format;
    protected transient List<RangeUtil> rangeInfoList;

    protected long timer = 0;
    protected long totalLength = 0;
    protected long downloadLength = 0;
    protected long remainingLength = 0;

    protected long remainingTime = 0;
    protected boolean downloading = false;

    public SessionReport() {
        super(new TotalSpeedMonitor());
        this.rangeInfoList = new LinkedList<>();
        this.format = new MessageFormat(pattern());
    }

    protected abstract String pattern();

    protected abstract Callable<String> updateDataCallable();

    public int rangeCount() {
        return rangeInfoList.size();
    }

    public boolean addRange(RangeUtil e) {
        return rangeInfoList.add(e);
    }

    public boolean removeRange(RangeUtil o) {
        return rangeInfoList.remove(o);
    }

    public void clear() {
        rangeInfoList.clear();
    }

    public RangeUtil get(int index) {
        return rangeInfoList.get(index);
    }

    public RangeUtil remove(int index) {
        return rangeInfoList.remove(index);
    }

    /*************************************/
    //////////////////////////////////////
    @Override
    public void snapshotPoint() {
        this.monitor.snapshotPoint();
    }

    @Override
    public synchronized void snapshotSpeed() {
        this.monitor.snapshotSpeed();
        totalLength = 0;
        downloadLength = 0;
        remainingLength = 0;
        rangeInfoList.forEach(info -> {
            info.oneCycleDataUpdate();
            totalLength += info.getFileLength();
            downloadLength += info.getDownloadLength();
            remainingLength += info.getRemainingLength();
        });
        downloading = this.monitor.getTcpDownloadSpeed() > 0;
    }

    protected long getTotalLength() {
        return totalLength;
    }

    protected long getDownloadLength() {
        return downloadLength;
    }

    protected long getRemainingLength() {
        return remainingLength;
    }

    public String getTotalLengthMB() {
        return unitLength(totalLength);
    }

    public String getDownloadLengthMB() {
        return unitLength(downloadLength);
    }

    public String getRemainingLengthMB() {
        return unitLength(remainingLength);
    }

    protected long getRemainingTime() {
        remainingTime = (getRemainingLength() + 1) / (this.monitor.getTcpDownloadSpeed() + 1);
        return remainingTime;
    }

    protected String getRemainingTimeString() {
        return green(underscore(Utils.timeFormat(getRemainingTime())));
    }

    public String getTimer() {
        return green(underscore(Utils.timeFormat(timer++)));
    }

    public String getPercent() {
        return Utils.percent(getDownloadLength(), getTotalLength());
    }

    protected float percent() {
        return (float) getDownloadLength() / getTotalLength();
    }

    public String progressLine(int width) {
        if (width < 45) {
            width = 66;
        } else {
            width -= 12;
        }

        int percent = (int) (width * percent());
        if (percent < 0) return "";
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(chars('=', percent));
        builder.append('>');
        builder.append(redLight(Utils.getRightString(getPercent(), 9)));
        builder.append(chars(' ', width - percent));
        builder.append(']');
        return builder.toString();
    }

    public void printMonitorReport() {
        printMonitorReport(System.out);
    }

    public void printMonitorReport(PrintStream out) {
        out.print(scheduledUpdate(updateDataCallable()));
    }

    protected <T> T scheduledUpdate(Callable<T> callable) {
        snapshotSpeed();
        T t = null;
        try {
            t = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        snapshotPoint();
        return t;
    }

    public boolean isDownloading() {
        return downloading;
    }

}
