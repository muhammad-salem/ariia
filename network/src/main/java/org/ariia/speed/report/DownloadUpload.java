package org.ariia.speed.report;

public interface DownloadUpload {
	long getTcpDownload();
	long getTcpUpload();
	long getUdpDownload();
	long getUdpUpload();
}
