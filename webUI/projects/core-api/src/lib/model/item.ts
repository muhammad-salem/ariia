import { RangeInfo } from './range-info';
import { Ui } from './ui';

export class Item implements Ui {
    id: string = '';
    url: string = '';
    redirectUrl: string = '';
    filename: string = '';
    state: string = '';
    saveDirectory: string = '';
    headers: Map<string, string[]> = new Map();
    rangeInfo: RangeInfo = new RangeInfo();
    tcpDownloadSpeed: number = 0;
    remainingTime: number = 0;

    //  implements Ui
    show: boolean;
    clicked: boolean;
    selected: boolean;

    constructor(item: Item) {
        if (item) {
            this.update(item);
        }

        this.show = true;
        this.clicked = false;
        this.selected = false;
    }

    update(item: Item) {
        this.id = item.id;
        this.url = item.url;
        this.redirectUrl = item.redirectUrl;
        this.filename = item.filename;
        this.state = item.state;
        this.saveDirectory = item.saveDirectory;
        this.rangeInfo.update(item.rangeInfo);
        this.tcpDownloadSpeed = item.tcpDownloadSpeed;
        this.remainingTime = item.remainingTime;
        Object.keys(item.headers).forEach((keys) => {
            this.headers.set(keys, item.headers[keys]);
        });
    }

    netwotkUpdate(item: Item) {
        this.state = item.state;
        this.rangeInfo.update(item.rangeInfo);
        this.tcpDownloadSpeed = item.tcpDownloadSpeed;
        this.remainingTime = item.remainingTime;
    }

    toString(): string {
        return `${this.filename} - ${this.rangeInfo.fileLength} - ${this.state}`;
    }
}