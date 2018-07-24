package org.okaria.range;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.okaria.Utils;
import org.okaria.speed.SpeedMonitor;
import org.terminal.Ansi;

public class RangeInfoMonitor extends SpeedMonitor implements List<RangeInfo> {

	protected List<RangeInfo> rangeInfos = new LinkedList<>();

	public RangeInfoMonitor() {
		rangeInfos = new LinkedList<>();
		useMinmallMode(false);
	}
	public RangeInfoMonitor(boolean minmal) {
		rangeInfos = new LinkedList<>();
		useMinmallMode(minmal);
	}
	
	@Override
	public int size() {
		return rangeInfos.size();
	}

	@Override
	public boolean isEmpty() {
		return rangeInfos.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return rangeInfos.contains(o);
	}

	@Override
	public Iterator<RangeInfo> iterator() {
		return rangeInfos.iterator();
	}

	@Override
	public Object[] toArray() {
		return rangeInfos.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return rangeInfos.toArray(a);
	}

	@Override
	public boolean add(RangeInfo e) {
		return rangeInfos.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return rangeInfos.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return rangeInfos.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends RangeInfo> c) {
		return rangeInfos.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends RangeInfo> c) {
		return rangeInfos.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return rangeInfos.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return rangeInfos.retainAll(c);
	}

	@Override
	public void clear() {
		rangeInfos.clear();
	}

	@Override
	public RangeInfo get(int index) {
		return rangeInfos.get(index);
	}

	@Override
	public RangeInfo set(int index, RangeInfo element) {
		return rangeInfos.set(index, element);
	}

	@Override
	public void add(int index, RangeInfo element) {
		rangeInfos.add(index, element);
	}

	@Override
	public RangeInfo remove(int index) {
		return rangeInfos.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return rangeInfos.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return rangeInfos.lastIndexOf(o);
	}

	@Override
	public ListIterator<RangeInfo> listIterator() {
		return rangeInfos.listIterator();
	}

	@Override
	public ListIterator<RangeInfo> listIterator(int index) {
		return rangeInfos.listIterator(index);
	}

	@Override
	public List<RangeInfo> subList(int fromIndex, int toIndex) {
		return rangeInfos.subList(fromIndex, toIndex);
	}

	/*************************************/
	//////////////////////////////////////

	protected long timer = 0;
	private Ansi ansi = new Ansi();
	private long totalLength = 0;
	private long downloadLength = 0;

	public void updateTotalLength() {
		totalLength = 0;
		rangeInfos.forEach(info -> {
			totalLength += info.getFileLength();
		});
	}

	public void updateDownloadLength() {
		downloadLength = 0;
		rangeInfos.forEach(info -> {
			downloadLength += info.getDownLength();
		});
	}

	protected long getTotalLength() {
		return totalLength;
	}

	protected long getDownloadLength() {
		return downloadLength;
	}

	public String getTotalLengthMB() {
		return toUnitLength(totalLength);
	}

	public String getDownloadLengthMB() {
		return toUnitLength(downloadLength);
	}

	private long getReminningTime() {
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}

	private String getReminningTimeString() {
		return ansi.green(ansi.Underscore(Utils.timeformate(getReminningTime())));
	}

	private long getRemainingLength() {
		return getTotalLength() - getDownloadLength();
	}
	
	public String getRemainingLengthMB() {
		return toUnitLength(getRemainingLength());
	}

	public String getTimer() {
		return ansi.green(ansi.Underscore(Utils.timeformate(timer++)));
	}

	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌
	public String getLengthInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.red(ansi.Bold(Utils.getStringWidth("T: " + getTotalLengthMB() , 20))));
		// builder.append(",\t");
		builder.append(ansi.yellow(ansi.Bold(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 20))));
		// builder.append(",\t");
		builder.append(ansi.magentaLight(ansi.Bold(Utils.getStringWidth("Down: " + getTotalReceiveMB(), 20))));

		return builder.toString();
	}

	public String getSpeedInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.blue(ansi.Bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 20))));
		// builder.append(",\t");
		builder.append(Utils.getStringWidth(ansi.red(ansi.Underscore(getPercent())), 30));
		return builder.toString();
	}

	/////////////////
	public String getReportLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.red(ansi.Bold(Utils.getStringWidth("T: " + getTotalLengthMB(), 16))));
		builder.append(ansi.magentaLight(ansi.Bold(Utils.getStringWidth("Down: " + getDownloadLengthMB(), 19))));
		builder.append(ansi.yellow(ansi.Bold(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 19))));
		builder.append(ansi.magentaLight(ansi.Bold(Utils.getStringWidth( "⇩ " + getTotalReceiveMB(), 15))));
		builder.append(ansi.blue(ansi.Bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 16))));
		builder.append(getReminningTimeString());
		return builder.toString();
	}

	//////////////////

	public String getReportLineFixedWidth() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				Utils.getStringWidth(ansi.red(ansi.Bold("Total : ") + ansi.Underscore(getDownloadLengthMB())), 48));
		builder.append(Utils
				.getStringWidth(ansi.yellow(ansi.Bold("Download : ") + ansi.Underscore(getTotalReceiveMB())), 50));
		builder.append(Utils.getStringWidth(
				ansi.yellow(ansi.Bold("Remaining : ") + ansi.Underscore(getRemainingLengthMB())), 50));
		builder.append(ansi.blue(ansi.Bold(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 22))));
		builder.append(Utils
				.getStringWidth(ansi.blue(ansi.Bold("↓ ") + ansi.Underscore(getSpeedTCPReceiveMB() + "/s")), 43));
		builder.append(ansi.red(ansi.Underscore(Utils.getStringWidth(getPercent(), 12))));
		builder.append("  " + getReminningTimeString() + "  ");
		// demondSpeedNow();
		return builder.toString();
	}

	public String getPercent() {
		return Utils.percent(getDownloadLength(), getTotalLength());
	}

	public String progressLine() {
		int percent = (int) (90 * (getDownloadLength() / (float) getTotalLength()));
		StringBuilder builder = new StringBuilder();
		builder.append(' ');
		int i = 0;
		for (; i < percent; i++) {
			builder.append('=');
		}
		builder.append("> ");
		builder.append(Utils.getStringWidth(ansi.red(getPercent()), 21));
		i++;
		for (; i <= 90; i++) {
			builder.append(' ');
		}
		return builder.toString();
	}

	protected String getPrintMessageLines() {
		return '\r' + Ansi.EraseLine + '\n' + Ansi.EraseLine + '\n' + Ansi.EraseLine + "\n\r" + Ansi.EraseLine
				+ "\r {0} [ {1} ] " + Ansi.EraseEndofLine + "\r\n" + Ansi.EraseLine + "\r {2} [ {3} ] "
				+ Ansi.EraseEndofLine + "\r\n" + Ansi.EraseLine + "\r [ {4} ] " + Ansi.EraseEndofLine + "\r\n"
				+ Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp;
	}

	protected String getPrintMessage() {
		return '\r' + Ansi.EraseDown
//				+ ansi.cursorDown(3)
				+ "\n\n\n"
//				+ ansi.cursorNextLine(3)
				+ getReport() + "\n\r" + ansi.cursorUp(5);
	}
	//os.name
	protected String getReport() {
		return "\r {0} [ {1} ] " + Ansi.EraseEndofLine + "\r\n" + Ansi.EraseLine + "\r [{2}] " + Ansi.EraseEndofLine;
	}

	private MessageFormat format;

	public void scheduledUpdate() {
		updateTotalLength();
		updateDownloadLength();
		demondSpeedNow();
	}
	
	public String getMointorPrintMessage() {
		updateTotalLength();
		updateDownloadLength();
		
		Object[] args = { getTimer(), getReportLine(), progressLine() };
		String message = format.format(args);
		demondSpeedNow();
		return message;
	}
	
	public void useMinmallMode(boolean minimal) {
		if (minimal) {
			format = new MessageFormat(getReport());
		}else {
			format = new MessageFormat(getPrintMessage());
		}
	}
	

	public String getMointorPrintMessageln() {
		return '\r' + getMointorPrintMessage() + Ansi.CursorUp;
	}

	@Override
	protected void finalize() throws Throwable {
		ansi = null;
		totalLength = 0;
		timer = 0;
		rangeInfos.clear();
	}
}
