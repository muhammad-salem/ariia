export class RangeInfo {
    fileLength: number;
    downloadLength: number;
    remainingLength: number;
    maxRangePoolNum: number;
    range: number[][];

    constructor() {
        this.fileLength = 0
        this.downloadLength = 0;
        this.remainingLength = 0;
        this.maxRangePoolNum = 0;
        this.range = [];
    }

    update(rangeInfo: RangeInfo){
        this.fileLength = rangeInfo.fileLength;
        this.downloadLength = rangeInfo.downloadLength;
        this.remainingLength = rangeInfo.remainingLength;
        this.maxRangePoolNum = rangeInfo.maxRangePoolNum;
        this.range = rangeInfo.range;
    }
}
