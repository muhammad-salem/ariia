package org.ariia.speed.report;

public class TotalSpeedReport<T extends TotalSpeedMonitor> extends SpeedReport<T> implements TotalMonitorReport {
	
	
	public TotalSpeedReport(T monitor) {
		this(monitor, true, true);
	}
	
	public TotalSpeedReport(T monitor, boolean isBinary, boolean isByte) {
		super(monitor, isBinary, isByte);
	}
	
	@Override
	public String getTotalDownload() {
		return unitLength(monitor.getTotalDownload());
	}

	@Override
	public String getTotalUpload() {
		return unitLength(monitor.getTotalUpload());
	}

	@Override
	public String getTotal() {
		return unitLength(monitor.getTotal());
	}

	@Override
	public String getTotalDownloadSpeed() {
		return unitLength(monitor.getTotalDownloadSpeed());
	}

	@Override
	public String getTotalUploadSpeed() {
		return unitLength(monitor.getTotalUploadSpeed());
	}

	@Override
	public String getTotalSpeed() {
		return unitLength(monitor.getTotalSpeed());
	}

}
