package org.ariia.speed.report;

public interface MonitorReport {
	String getTcpDownload();
	String getTcpUpload();
	String getUdpDownload();
	String getUdpUpload();
	
	String getTcpDownloadSpeed();
	String getTcpUploadSpeed();
	String getUdpDownloadSpeed();
	String getUdpUploadSpeed();
}
