export interface NetworkConfig {
	isBinary: boolean;
	isByte: boolean;
}

export interface NetworkMonitor {
	tcpDownload: number;
	tcpUpload: number;
	udpDownload: number;
	udpUpload: number;
}

export interface SpeedMonitor extends NetworkMonitor {
	tcpDownloadSpeed: number;
	tcpUploadSpeed: number;
	udpDownloadSpeed: number;
	udpUploadSpeed: number;
}

export interface TotalSpeedMonitor extends SpeedMonitor {
	totalDownload: number;
	totalUpload: number;
	total: number;
	totalDownloadSpeed: number;
	totalUploadSpeed: number;
	totalSpeed: number;
}

export interface SpeedReport<T extends SpeedMonitor> {
	monitor: T;
}

export interface TotalSpeedReport<T extends TotalSpeedMonitor> extends SpeedReport<T> {
	/*	now the type of monitor: T; is 'TotalSpeedMonitor'*/
}


export interface RangeReport extends SpeedReport<SpeedMonitor> {
	remainingTime: number;
	downloading: boolean;
}

export interface SessionReport extends TotalSpeedReport<TotalSpeedMonitor> {
	timer: number;
	totalLength: number;
	downloadLength: number;
	remainingLength: number;
	remainingTime: number;
	downloading: boolean;
}

export class SimpleRangeReport implements RangeReport {
	downloading = false;
	remainingTime = 0;
	monitor = {
		tcpDownload: 0,
		tcpUpload: 0,
		udpDownload: 0,
		udpUpload: 0,
		tcpDownloadSpeed: 0,
		tcpUploadSpeed: 0,
		udpDownloadSpeed: 0,
		udpUploadSpeed: 0
	};
}

export class SimpleSessionReport implements SessionReport {
	timer = 0;
	totalLength = 0;
	downloadLength = 0;
	remainingLength = 0;
	downloading = false;
	remainingTime = 0;
	monitor = {
		tcpDownload: 0,
		tcpUpload: 0,
		udpDownload: 0,
		udpUpload: 0,
		tcpDownloadSpeed: 0,
		tcpUploadSpeed: 0,
		udpDownloadSpeed: 0,
		udpUploadSpeed: 0,
		totalDownload: 0,
		totalUpload: 0,
		total: 0,
		totalDownloadSpeed: 0,
		totalUploadSpeed: 0,
		totalSpeed: 0
	};
}

