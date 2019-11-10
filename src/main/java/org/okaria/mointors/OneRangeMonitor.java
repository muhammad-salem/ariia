package org.okaria.mointors;

import org.okaria.range.RangeUtil;
import org.okaria.speed.SpeedMonitor;
import org.okaria.util.Utils;

public class OneRangeMonitor extends SpeedMonitor {
	
	protected RangeUtil info;
	protected String    name;
	
	
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
		info.oneCycleDataUpdate();
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
		return (getRemainingLength() + 1) / (speedOfTCPReceive() + 1);
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
	
//	public String getTimer() {
//		return Utils.timeformate(timer);
//	}
	
}
