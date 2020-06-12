package org.ariia.monitors;

import org.ariia.range.RangeUtil;
import org.ariia.speed.SpeedMonitor;
import org.ariia.util.Utils;

public class OneRangeMonitor extends SpeedMonitor {
	
	protected RangeUtil info;
	protected String    name;
	protected long reminningTime;
	
	public OneRangeMonitor(RangeUtil info, String name) {
		this.info = info;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public RangeUtil getRangeUtil() {
		return info;
	}
	
	public void updateData() {
		snapshotSpeed();
		updateTotal();
		info.oneCycleDataUpdate();
		reminningTime = (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
	}

	public long getTotalLength() {
		return info.getFileLength();
	}

	public long getDownloadLength() {
		return info.getDownloadLength();
	}

	public String getTotalLengthMB() {
		return info.getFileLengthMB();
	}

	public String getDownloadLengthMB() {
		return info.getDownloadLengthMB();
	}

	public long getReminningTime() {
		return reminningTime;
	}

	public String getReminningTimeString() {
		return Utils.timeformate(getReminningTime());
	}

	public long getRemainingLength() {
		return info.getRemainingLength();
	}

	public String getRemainingLengthMB() {
		return info.getRemainingLengthMB();
	}
	
	public String getPercent() {
		return Utils.percent(getDownloadLength(), getTotalLength());
	}
	protected float percent() {
		return (float) getDownloadLength() /  getTotalLength();
	}
	
}
