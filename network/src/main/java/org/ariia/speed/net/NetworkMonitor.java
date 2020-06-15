package org.ariia.speed.net;

import org.ariia.speed.report.DownloadUpload;

public class NetworkMonitor implements SocketMonitor, DatagramSocketMonitor, DownloadUpload {
	
	protected long tcpDownload = 0;
	protected long tcpUpload = 0;
	protected long udpDownload = 0;
	protected long udpUpload = 0;
	

	@Override
	public void onRead(IntWarp len) {
		tcpDownload += len.value;
	}

	@Override
	public void onWrite(IntWarp len) {
		tcpUpload += len.value;
	}
	
	@Override
	public void onReceive(final IntWarp len) {
		udpDownload += len.value;
	}

	@Override
	public void onSend(final IntWarp len) {
		udpUpload += len.value;
	}

	@Override
	public long getTcpDownload() {
		return tcpDownload;
	}

	@Override
	public long getTcpUpload() {
		return tcpUpload;
	}

	@Override
	public long getUdpDownload() {
		return udpDownload;
	}

	@Override
	public long getUdpUpload() {
		return udpUpload;
	}
	
}
