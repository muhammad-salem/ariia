package org.okaria.mointors;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.terminal.console.log.Log;
import org.okaria.range.RangeInfo;
import org.okaria.speed.SpeedMonitor;
import org.okaria.util.Utils;
import org.terminal.Ansi;

public class RangesInfoMonitor extends SpeedMonitor {

	private static  Ansi ansi = new Ansi();
	protected MessageFormat format;
	protected List<RangeInfo> rangeInfos;

	public RangesInfoMonitor() {
		rangeInfos = new LinkedList<>();
		format = new MessageFormat(newFormatMessage());
	}
	public int size() {
		return rangeInfos.size();
	}
	public boolean isEmpty() {
		return rangeInfos.isEmpty();
	}
	public boolean add(RangeInfo e) {
		return rangeInfos.add(e);
	}
	public boolean remove(Object o) {
		return rangeInfos.remove(o);
	}
	public void clear() {
		rangeInfos.clear();
	}
	public RangeInfo get(int index) {
		return rangeInfos.get(index);
	}
	public RangeInfo remove(int index) {
		return rangeInfos.remove(index);
	}

	/*************************************/
	//////////////////////////////////////

	protected long timer = 0;
	private long totalLength = 0;
	private long downloadLength = 0;
	private long remainingLength = 0;

	protected void updateRangeInfoData() {
		totalLength = 0;
		downloadLength = 0;
		remainingLength = 0;
		rangeInfos.forEach(info -> {
			info.oneCycleDataUpdate();
			totalLength += info.getFileLength();
			downloadLength += info.getDownloadLength();
			remainingLength += info.getRemainingLength();
		});
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

	public String getTotalLengthMB() { return toUnitLengthBytes(totalLength); }
	public String getDownloadLengthMB() { return toUnitLengthBytes(downloadLength); }
	public String getRemainingLengthMB() { return toUnitLengthBytes(remainingLength); }
	
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
		width -= 12;
		int percent = (int)(width * percent());
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		builder.append(ansi.chars('=', percent));
		builder.append('>');
		builder.append(ansi.redLight (Utils.getRightString(getPercent(), 9)));
		builder.append(ansi.chars(' ', width - percent));
		builder.append(']');
		return builder.toString();
	}
	


	public String getMointorReport() {
//		updateRangeInfoData();
//		String message = newReport();
//		demondSpeedNow();
//		return message;
		return scheduledUpdate(this::newReport);
//		return scheduledUpdate(this::formateReport);
	}
	
	//////////////////
	private String formateReport() {
		Object[] args = { getTimer(), getReportLine(), progressLine(80) };
		return format.format(args);
	}
	
	protected String getPrintMessage() {
		StringBuilder builder = new  StringBuilder();
		builder.append('\r');
		builder.append( Ansi.EraseDown );
		builder.append("\n\n\n");
		builder.append("\r {0} [ {1} ]" );
		builder.append(Ansi.EraseEndofLine );
		builder.append("\r\n");
		builder.append(Ansi.EraseLine);
		builder.append("\r{2}");
		builder.append( Ansi.EraseEndofLine);
		builder.append("\n\r");
		builder.append(ansi.cursorUp(5));
		return builder.toString();
	}
	
	
	/////////////////
	public String getReportLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.red(ansi.bold(Utils.getStringWidth("T: " + getTotalLengthMB(), 16))));
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth("Down: " + getDownloadLengthMB(), 19))));
		builder.append(ansi.yellow(ansi.bold(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 19))));
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth( "⇩ " + getTotalReceiveMB(), 15))));
		builder.append(ansi.blue(ansi.bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "ps", 16))));
		builder.append(getRemainingTimeString());
		return builder.toString();
	}
	/////////////////////////

	private String newFormatMessage() {
		StringBuilder builder = new  StringBuilder();
		builder.append('\r');
		
		builder.append( Ansi.EraseDown );
		builder.append("\n\n\n");
		builder.append("\r {0} [ {1} ]" );
		builder.append(Ansi.EraseEndofLine );
		
		builder.append("\r\n");
		builder.append("\r {2} [ {3} ]" );
		builder.append(Ansi.EraseEndofLine );
		builder.append("\r\n");
		
		builder.append(Ansi.EraseLine);
		builder.append("\r{4}");
		builder.append( Ansi.EraseEndofLine);
		builder.append("\n\r");
		builder.append(ansi.cursorUp(6));
		return builder.toString();
	}
	
	public String newFormatMessageReport() {
		return scheduledUpdate(this::formateReport);
	}
	
	private String newReport() {
		Object[] args = { getTimer(), firstLine(), getRemainingTimeString(), secondLine(), progressLine(78) };
		return format.format(args);
	}
	
	private String firstLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.red(ansi.bold(Utils.getStringWidth("T: " + getTotalLengthMB(), 16))));
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth("Down: " + getDownloadLengthMB(), 19))));
		builder.append(ansi.yellow(ansi.bold(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 19))));
		return builder.toString();
	}
	
	private String secondLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.magentaLight(ansi.bold(Utils.getStringWidth( "⇩ " + getTotalReceiveMB(), 15))));
		builder.append(ansi.blue(ansi.bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "ps", 16))));
		return builder.toString();
	}
	
	
	public <T> T scheduledUpdate(Callable<T> callable) {
		updateRangeInfoData();
		T t = null;
		try {
			t = callable.call();
		} catch (Exception e) {
			Log.error(getClass(), e.getClass().getName(), e.getMessage());
		}
		demondSpeedNow();
		return t;
	}
	
//	public String getMointorPrintMessage() {
//		updateRangeInfoData();
//		String message = formateReport();
//		demondSpeedNow();
//		return message;
//	}


}
