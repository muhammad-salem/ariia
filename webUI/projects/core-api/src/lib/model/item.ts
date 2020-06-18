import { RangeInfo } from './range-info';
import { SimpleRangeReport, RangeReport } from './network-session';

export class Item {

    id: string = '';
    url: string = '';
    redirectUrl: string = '';
    filename: string = '';
    state: string = '';
    saveDirectory: string = '';
    headers: Map<string, string[]> = new Map();

    rangeInfo: RangeInfo = new RangeInfo();
    report: RangeReport = new SimpleRangeReport();

    constructor(item: Item) {
        if (item) {
            this.update(item);
        }
        if (!this.report) {
            this.report = new SimpleRangeReport();
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
        if (item.report) {
            this.report.update(item.report)
        }
    }

    toString(): string {
        return `${this.filename} - ${this.rangeInfo.fileLength} - ${this.state}`;
    }
}
