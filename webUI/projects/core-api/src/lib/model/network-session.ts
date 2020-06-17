export interface NetworkConfig {
    isBinary: boolean;
    isByte: boolean;
}

export interface NetworkMointor {
    tcpDownloadSpeed: number;
    tcpUploadSpeed: number;
    udpDownloadSpeed: number;
    udpUploadSpeed: number;
    tcpDownload: number;
    tcpUpload: number;
    udpDownload: number;
    udpUpload: number;
}

export class ItemSession {

    timer: number;
    remainingTime: number;
    downloading: boolean;
    mointor: NetworkMointor;

    update(session: ItemSession) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }
}

export interface TotalNetworkMointor extends NetworkMointor {
    totalDownload: number;
    totalUpload: number;
    total: number;
    totalDownloadSpeed: number;
    totalUploadSpeed: number;
    totalSpeed: number;
}

export class NetworkSession {

    timer: number;
    totalLength: number;
    downloadLength: number;
    remainigLength: number;
    remainingTime: number;
    downloading: boolean;
    mointor: TotalNetworkMointor;

    update(session: NetworkSession) {
        if (session) {
            Object.keys(session).forEach(key => {
                this[key] = session[key];
            });
        }
    }
}
