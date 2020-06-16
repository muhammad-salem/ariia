package org.ariia.monitors;

import java.util.Objects;

import org.ariia.range.RangeUtil;
import org.ariia.speed.report.SpeedMonitor;
import org.ariia.speed.report.SpeedReport;
import org.ariia.util.Utils;

public class RangeMonitor extends SpeedMonitor {
	
	protected RangeUtil info;
	protected String    name;
	protected long remainingTime;
	
	protected transient SpeedReport<SpeedMonitor> speedReport;
	
	public RangeMonitor(RangeUtil info, String name) {
		this.info = Objects.requireNonNull(info);
		this.name = Objects.requireNonNull(name);
		this.speedReport = new SpeedReport<SpeedMonitor>(this);
	}
	
	
	public String getName() {
		return name;
	}
	
	public RangeUtil getRangeUtil() {
		return info;
	}
	
	public SpeedReport<SpeedMonitor> getSpeedReport() {
		return speedReport;
	}
	
	@Override
	public void snapshotSpeed() {
		super.snapshotSpeed();
		info.oneCycleDataUpdate();
		remainingTime = (info.getRemainingLength() + 1) / (speedReport.getMointor().getTcpDownloadSpeed() + 1);
	}

	public long getRemainingTime() {
		return remainingTime;
	}

	public long getRemainingLength() {
		return info.getRemainingLength();
	}

	public long getTotalLength() {
		return info.getFileLength();
	}

	public long getDownloadLength() {
		return info.getDownloadLength();
	}

	public String getTotalLengthMB() {
		return speedReport.unitLength(info.getFileLength());
	}

	public String getDownloadLengthMB() {
		return speedReport.unitLength(info.getDownloadLength());
	}

	public String getRemainingLengthMB() {
		return speedReport.unitLength(info.getRemainingLength());
	}

	public String getRemainingTimeString() {
		return Utils.timeformate(getRemainingTime());
	}
	
	public String getPercent() {
		return Utils.percent(info.getDownloadLength(), info.getFileLength());
	}
	protected float percent() {
		return (float) info.getDownloadLength() /  info.getFileLength();
	}
	
}
