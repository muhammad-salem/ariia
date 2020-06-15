package org.ariia.speed.report;

public class TotalSpeedMonitor extends SpeedMonitor implements DownloadUploadTotal, DownloadUploadTotalSpeed {
	
	protected long totalDownload = 0;
	protected long totalUpload = 0;
	protected long total;
	
	protected long totalDownloadSpeed = 0;
	protected long totalUploadSpeed = 0;
	protected long totalSpeed;
	
	@Override
	public void snapshotSpeed() {
		super.snapshotSpeed();
		totalDownload = tcpDownload + udpDownload;
		totalUpload  = tcpUpload  + udpUpload;
		total = totalDownload + totalUpload;
		
		totalDownloadSpeed = tcpDownloadSpeed + udpDownloadSpeed;
		totalUploadSpeed  = tcpUploadSpeed  + udpUploadSpeed;
		totalSpeed = totalDownloadSpeed + totalUploadSpeed;
		
	}

	@Override
	public long getTotalDownloadSpeed() {
		return totalDownloadSpeed;
	}

	@Override
	public long getTotalUploadSpeed() {
		return totalUploadSpeed;
	}

	@Override
	public long getTotalSpeed() {
		return totalSpeed;
	}

	@Override
	public long getTotalDownload() {
		return totalDownload;
	}

	@Override
	public long getTotalUpload() {
		return totalUpload;
	}

	@Override
	public long getTotal() {
		return total;
	}
}
