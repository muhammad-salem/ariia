package org.ariia.speed.report;

import org.ariia.speed.net.NetworkMonitor;

public class SpeedMonitor extends NetworkMonitor implements SpeedSnapshot, DownloadUploadSpeed {

    protected transient long tcpDownloadOld = 0;
    protected transient long tcpUploadOld = 0;
    protected transient long udpDownloadOld = 0;
    protected transient long udpUploadOld = 0;

    protected long tcpDownloadSpeed = 0;
    protected long tcpUploadSpeed = 0;
    protected long udpDownloadSpeed = 0;
    protected long udpUploadSpeed = 0;


    /**
     * Call this method to snapshot old vales of Send and Received of TCP and UDP transfer.
     */
    @Override
    public void snapshotPoint() {
        tcpDownloadOld = tcpDownload;
        tcpUploadOld = tcpUpload;
        udpDownloadOld = udpDownload;
        udpUploadOld = udpUpload;
    }

    @Override
    public void snapshotSpeed() {
        tcpDownloadSpeed = tcpDownload - tcpDownloadOld;
        tcpUploadSpeed = tcpUpload - tcpUploadOld;
        udpDownloadSpeed = udpDownload - udpDownloadOld;
        udpUploadSpeed = udpUpload - udpUploadOld;
    }

    @Override
    public long getTcpDownloadSpeed() {
        return tcpDownloadSpeed;
    }

    @Override
    public long getTcpUploadSpeed() {
        return tcpUploadSpeed;
    }

    @Override
    public long getUdpDownloadSpeed() {
        return udpDownloadSpeed;
    }

    @Override
    public long getUdpUploadSpeed() {
        return udpUploadSpeed;
    }
}
