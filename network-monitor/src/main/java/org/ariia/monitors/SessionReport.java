package org.ariia.monitors;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ariia.range.RangeUtil;
import org.ariia.speed.report.SpeedSnapshot;
import org.ariia.speed.report.TotalSpeedMonitor;
import org.ariia.speed.report.TotalSpeedReport;
import org.ariia.util.Utils;
import org.terminal.ansi.CursorControl;
import org.terminal.ansi.Styles;

public abstract class SessionReport extends TotalSpeedReport<TotalSpeedMonitor> implements SpeedSnapshot, Styles, CursorControl {

	protected transient  MessageFormat format;
	protected transient List<RangeUtil> rangeInfos;

	protected long timer = 0;
	protected long totalLength = 0;
	protected long downloadLength = 0;
	protected long remainigLength = 0;

	protected long remainingTime = 0;
	private boolean downloading = false;
	
	public SessionReport() {
		super(new TotalSpeedMonitor());
		this.rangeInfos = new LinkedList<>();
		this.format = new MessageFormat(pattern());
	}

	protected abstract String pattern();
	protected abstract Callable<String> updateDataCallable();
	
	public int rangeCount() {
		return rangeInfos.size();
	}
	public boolean addRange(RangeUtil e) {
		return rangeInfos.add(e);
	}
	public boolean removeRange(RangeUtil o) {
		return rangeInfos.remove(o);
	}
	public void clear() {
		rangeInfos.clear();
	}
	public RangeUtil get(int index) {
		return rangeInfos.get(index);
	}
	public RangeUtil remove(int index) {
		return rangeInfos.remove(index);
	}

	/*************************************/
	//////////////////////////////////////
	


	@Override
	public void snapshotPoint() {
		this.mointor.snapshotPoint();
	}

	@Override
	public synchronized void snapshotSpeed() {
		this.mointor.snapshotSpeed();
		totalLength = 0;
		downloadLength = 0;
		remainigLength = 0;
		rangeInfos.forEach(info -> {
			info.oneCycleDataUpdate();
			totalLength += info.getFileLength();
			downloadLength += info.getDownloadLength();
			remainigLength += info.getRemainingLength();
		});
		downloading = this.mointor.getTcpDownloadSpeed() > 0;
	}

	protected long getTotalLength() {
		return totalLength;
	}
	protected long getDownloadLength() {
		return downloadLength;
	}
	protected long getRemainingLength() {
		return remainigLength;
	}

	public String getTotalLengthMB() { return unitLength(totalLength); }
	public String getDownloadLengthMB() { return unitLength(downloadLength); }
	public String getRemainingLengthMB() { return unitLength(remainigLength); }
	
	protected long getRemainingTime() {
		remainingTime = (getRemainingLength() + 1) / (this.mointor.getTcpDownloadSpeed() + 1);
		return remainingTime;
	}
	protected String getRemainingTimeString() {
		return green(underscore(Utils.timeformate(getRemainingTime())));
	}
	public String getTimer() {
		return green(underscore(Utils.timeformate(timer++)));
	}

	public String getPercent() {
		return Utils.percent(getDownloadLength(), getTotalLength());
	}
	protected float percent() {
		return (float) getDownloadLength() /  getTotalLength();
	}
	
	public String progressLine(int width) {
		if(width < 45) {
			width = 66;
		}else {
			width -= 12;
		}
		
		int percent = (int)(width * percent());
		if(percent < 0) return "";
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append(chars('=', percent));
		builder.append('>');
		builder.append(redLight (Utils.getRightString(getPercent(), 9)));
		builder.append(chars(' ', width - percent));
		builder.append(']');
		return builder.toString();
	}
	
	public void printMointorReport() {
		printMointorReport(System.out);
	}
	
	public void printMointorReport(PrintStream out) {
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
