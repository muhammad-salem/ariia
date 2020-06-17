import { RangeInfo } from './range-info';
import { ItemSession } from './network-session';

export class Item {
    id: string = '';
    url: string = '';
    redirectUrl: string = '';
    filename: string = '';
    state: string = '';
    saveDirectory: string = '';
    headers: Map<string, string[]> = new Map();
    tcpDownloadSpeed: number = 0;
    remainingTime: number = 0;
    downloading: boolean;

    rangeInfo: RangeInfo = new RangeInfo();
    report: ItemSession = new ItemSession();

    constructor(item: Item) {
        if (item) {
            this.update(item);
        }
    }

    update(item: Item) {
        this.id = item.id;
        this.url = item.url;
        this.redirectUrl = item.redirectUrl;
        this.filename = item.filename;
        this.saveDirectory = item.saveDirectory;
        this.netwotkUpdate(item);
        Object.keys(item.headers).forEach((keys) => {
            this.headers.set(keys, item.headers[keys]);
        });
    }

    netwotkUpdate(item: Item) {
        this.state = item.state;
        this.rangeInfo.update(item.rangeInfo);
        this.tcpDownloadSpeed = item.tcpDownloadSpeed;
        this.remainingTime = item.remainingTime;
        this.downloading = item.downloading;
    }

    toString(): string {
        return `${this.filename} - ${this.rangeInfo.fileLength} - ${this.state}`;
    }
}
