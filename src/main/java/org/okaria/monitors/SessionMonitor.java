package org.okaria.monitors;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.terminal.console.log.Log;
import org.okaria.range.RangeUtil;
import org.okaria.speed.SpeedMonitor;
import org.okaria.util.Utils;
import org.terminal.Ansi;

public abstract class SessionMonitor extends SpeedMonitor {

	protected static  Ansi ansi = Utils.ANSI;
	protected MessageFormat format;
	protected List<RangeUtil> rangeInfos = new LinkedList<>();

	public SessionMonitor() {
		rangeInfos = new LinkedList<>();
		format = new MessageFormat(pattern());
	}

	protected abstract String pattern();
	protected abstract Callable<String> updateDataCallable();
	
	public int size() {
		return rangeInfos.size();
	}
	public boolean isEmpty() {
		return rangeInfos.isEmpty();
	}
	public boolean add(RangeUtil e) {
		return rangeInfos.add(e);
	}
	public boolean remove(Object o) {
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

	protected long timer = 0;
	private long totalLength = 0;
	private long downloadLength = 0;
	private long remainigLength = 0;
	
	private boolean downloading = false;

	protected synchronized void rangeInfoUpdateData() {
		totalLength = 0;
		downloadLength = 0;
		remainigLength = 0;
		rangeInfos.forEach(info -> {
			info.oneCycleDataUpdate();
			totalLength += info.getFileLength();
			downloadLength += info.getDownloadLength();
			remainigLength += info.getRemainingLength();
		});
		downloading = speedOfTCPReceive() > 0;
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

	public String getTotalLengthMB() { return toUnitLengthBytes(totalLength); }
	public String getDownloadLengthMB() { return toUnitLengthBytes(downloadLength); }
	public String getRemainingLengthMB() { return toUnitLengthBytes(remainigLength); }
	
	protected long getRemainingTime() {
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}
	protected String getRemainingTimeString() {
		return ansi.green(ansi.underscore(Utils.timeformate(getRemainingTime())));
	}
	public String getTimer() {
		return ansi.green(ansi.underscore(Utils.timeformate(timer++)));
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
		builder.append(ansi.chars('=', percent));
		builder.append('>');
		builder.append(ansi.redLight (Utils.getRightString(getPercent(), 9)));
		builder.append(ansi.chars(' ', width - percent));
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
		rangeInfoUpdateData();
		T t = null;
		try {
			t = callable.call();
		} catch (Exception e) {
			Log.error(getClass(), e.getClass().getSimpleName(), e.getMessage());
		}
		demondSpeedNow();
		return t;
	}



	public boolean isDownloading() {
		return downloading;
	}
	
}
