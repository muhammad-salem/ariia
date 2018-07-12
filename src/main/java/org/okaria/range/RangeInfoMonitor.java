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

	private void updateTotalLength() {
		totalLength = 0;
		rangeInfos.forEach(info -> {
			totalLength += info.getFileLength();
		});
	}

	private void updateDownloadLength() {
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

	protected String getTotalLengthMB() {
		return toUnitLength(totalLength);
	}

	protected String getDownloadLengthMB() {
		return toUnitLength(downloadLength);
	}

	private long getReminningTime() {
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}

	private String getReminningTimeString() {
		return ansi.Green(ansi.Underscore(Utils.timeformate(getReminningTime())));
	}

	private long getRemainingLength() {
		return getTotalLength() - getDownloadLength();
	}

	private String getRemainingLengthMB() {
		return toUnitLength(getRemainingLength());
	}

	public String getTimer() {
		return ansi.Green(ansi.Underscore(Utils.timeformate(timer++)));
	}

	// ↓↑⇔⇧⇩⇅⛗⌚▽△▲▼⬆⬇⬌
	public String getLengthInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.Red(ansi.Bright(Utils.getStringWidth("T: " + getTotalLengthMB() , 20))));
		// builder.append(",\t");
		builder.append(ansi.Yellow(ansi.Bright(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 20))));
		// builder.append(",\t");
		builder.append(ansi.MagentaLight(ansi.Bright(Utils.getStringWidth("Down: " + getTotalReceiveMB(), 20))));

		return builder.toString();
	}

	public String getSpeedInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.Blue(ansi.Bright(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 20))));
		// builder.append(",\t");
		builder.append(Utils.getStringWidth(ansi.Red(ansi.Underscore(getPercent())), 30));
		return builder.toString();
	}

	/////////////////
	public String getReportLine() {
		StringBuilder builder = new StringBuilder();
		builder.append(ansi.Red(ansi.Bright(Utils.getStringWidth("T: " + getTotalLengthMB(), 16))));
		builder.append(ansi.MagentaLight(ansi.Bright(Utils.getStringWidth("Down: " + getDownloadLengthMB(), 19))));
		builder.append(ansi.Yellow(ansi.Bright(Utils.getStringWidth("Remain: " + getRemainingLengthMB(), 19))));
		builder.append(ansi.MagentaLight(ansi.Bright(Utils.getStringWidth( "⇩ " + getTotalReceiveMB(), 15))));
		builder.append(ansi.Blue(ansi.Bright(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 16))));
		builder.append(getReminningTimeString());
		return builder.toString();
	}

	//////////////////

	public String getReportLineFixedWidth() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				Utils.getStringWidth(ansi.Red(ansi.Bright("Total : ") + ansi.Underscore(getDownloadLengthMB())), 48));
		builder.append(Utils
				.getStringWidth(ansi.Yellow(ansi.Bright("Download : ") + ansi.Underscore(getTotalReceiveMB())), 50));
		builder.append(Utils.getStringWidth(
				ansi.Yellow(ansi.Bright("Remaining : ") + ansi.Underscore(getRemainingLengthMB())), 50));
		builder.append(ansi.Blue(ansi.Bright(Utils.getStringWidth("↓ " + getSpeedTCPReceiveMB() + "/s", 22))));
		builder.append(Utils
				.getStringWidth(ansi.Blue(ansi.Bright("↓ ") + ansi.Underscore(getSpeedTCPReceiveMB() + "/s")), 43));
		builder.append(ansi.Red(ansi.Underscore(Utils.getStringWidth(getPercent(), 12))));
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
		int i = 0;
		for (; i < percent; i++) {
			builder.append('=');
		}
		builder.append("> ");
		builder.append(Utils.getStringWidth(ansi.Red(getPercent()), 21));
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
		return '\r' + Ansi.EraseLine + '\n' + Ansi.EraseLine + '\n' + Ansi.EraseLine + "\n\r" + Ansi.EraseLine
				+ "\r {0} [ {1} ] " + Ansi.EraseEndofLine + "\r\n" + Ansi.EraseLine + "\r [{2}] " + Ansi.EraseEndofLine
				+ "\r\n" + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp + Ansi.CursorUp;
	}

	private MessageFormat format = new MessageFormat(getPrintMessage());

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

	public String getMointorPrintMessageln() {
		return '\r' + getMointorPrintMessage() + Ansi.CursorUp;
	}

	@Override
	protected void finalize() throws Throwable {
		ansi = null;
		totalLength = 0;
		timer = 0;
		rangeInfos.clear();
		super.finalize();
	}
}
