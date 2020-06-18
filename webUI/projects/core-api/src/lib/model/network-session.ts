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
    //	now the type of monitor: T; is 'TotalSpeedMonitor'
}


export interface RangeReport extends SpeedReport<SpeedMonitor> {
    remainingTime: number;
    downloading: boolean;

    update(report: RangeReport): void;
}

export class SimpleRangeReport implements RangeReport {
    remainingTime: number;
    downloading: boolean;
    monitor: SpeedMonitor;

    constructor() {
        this.remainingTime = 0;
        this.downloading = false;
        this.monitor = {
            tcpDownloadSpeed: 0,
            tcpUploadSpeed: 0,
            udpDownloadSpeed: 0,
            udpUploadSpeed: 0,
            tcpDownload: 0,
            tcpUpload: 0,
            udpDownload: 0,
            udpUpload: 0
        };
    }

    update(session: RangeReport) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }
}

export interface SessionReport extends TotalSpeedReport<TotalSpeedMonitor> {
    timer: number;
    totalLength: number;
    downloadLength: number;
    remainingLength: number;
    remainingTime: number;
    downloading: boolean;

    update(report: SessionReport): void;
}

export class SimpleSessionReport implements SessionReport {
    timer: number;
    totalLength: number;
    downloadLength: number;
    remainingLength: number;
    remainingTime: number;
    downloading: boolean;
    monitor: TotalSpeedMonitor;

    constructor() {
        this.timer = 0;
        this.totalLength = 0;
        this.downloadLength = 0;
        this.remainingLength = 0;
        this.remainingTime = 0;
        this.downloading = false;
        this.monitor = {
            tcpDownloadSpeed: 0,
            tcpUploadSpeed: 0,
            udpDownloadSpeed: 0,
            udpUploadSpeed: 0,
            tcpDownload: 0,
            tcpUpload: 0,
            udpDownload: 0,
            udpUpload: 0,
            totalDownload: 0,
            totalUpload: 0,
            total: 0,
            totalDownloadSpeed: 0,
            totalUploadSpeed: 0,
            totalSpeed: 0
        };
    }

    update(session: SessionReport) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }

}




////////////////////////////////
/*
export interface SpeedReport {
	monitor: SpeedMonitor;
}


export interface TotalSpeedReport extends SpeedReport {

}

export class RangeReport {

    remainingTime: number = 0;
    downloading: boolean = false;
    monitor: NetworkMonitor = {
		tcpDownloadSpeed: 0,
		tcpUploadSpeed: 0,
		udpDownloadSpeed: 0,
		udpUploadSpeed: 0,
		tcpDownload: 0,
		tcpUpload: 0,
		udpDownload: 0,
		udpUpload: 0
    };

    update(session: ItemSession) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }
}

export interface TotalSpeedMonitor extends NetworkMonitor {
    totalDownload: number;
    totalUpload: number;
    total: number;
    totalDownloadSpeed: number;
    totalUploadSpeed: number;
    totalSpeed: number;
}


export class SessionReport {

    timer: number;
    totalLength: number;
    downloadLength: number;
    remainingLength: number;
    remainingTime: number;
    downloading: boolean;
    monitor: TotalSpeedMonitor = {
		tcpDownloadSpeed: 0,
		tcpUploadSpeed: 0,
		udpDownloadSpeed: 0,
		udpUploadSpeed: 0,
		tcpDownload: 0,
		tcpUpload: 0,
		udpDownload: 0,
		udpUpload: 0,
		totalDownload: 0,
		totalUpload: 0,
		total: 0,
		totalDownloadSpeed: 0,
		totalUploadSpeed: 0,
		totalSpeed: 0
	};

    update(session: NetworkSession) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }
}

*/
