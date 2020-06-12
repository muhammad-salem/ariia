import { Injectable } from '@angular/core';
import { RangeInfo } from '../model/range-info';

@Injectable({
  providedIn: 'root'
})
export class RangeService {

	constructor() { }

	indexOf(rangeInfo: RangeInfo, index: number): number[] {
		return rangeInfo.range[index];
	}
	
	remainLengthOf(rangeInfo: RangeInfo, index: number): number {
		return rangeInfo.range[index][1] - rangeInfo.range[index][0];
	}


	private isArrayFinish(ls: number[]): boolean {
		return (ls[1] != -1) && (ls[0] - ls[1] >= 0);
	}
	
	isFinish(rangeInfo: RangeInfo, index: number): boolean {
		return this.isArrayFinish(this.indexOf(rangeInfo ,index));
	}

	isRangeFinish(rangeInfo: RangeInfo): boolean {
		for ( var index = 0; index < rangeInfo.range.length; index++) {
			if (!this.isFinish(rangeInfo, index)){
				return false; // at least one array not yet had finished
			}
		}
		return true;
	}
  
	isStreaming(rangeInfo: RangeInfo): boolean {
		return (rangeInfo.range.length === 1) && (rangeInfo.range[0][0] >= 0);
	}

	hadLength(rangeInfo: RangeInfo): boolean {
		return rangeInfo.range.length == 1 && rangeInfo.range[0][1] == -1;
	}
	
	getIJ(rangeInfo: RangeInfo, i: number, j: number): number {
		return rangeInfo.range[i][j];
	}

	startOfIndex(rangeInfo: RangeInfo, index: number): number {
		return rangeInfo.range[index][0];
	}

	limitOfIndex(rangeInfo: RangeInfo, index: number): number {
		return rangeInfo.range[index][1];
	}

	percent(rangeInfo: RangeInfo): number {
		return (rangeInfo.downloadLength / rangeInfo.fileLength) * 100;
	}

	rangePercent(rangeInfo: RangeInfo, index: number): number {
		let limit = 0;
		if (index < 2) {
			limit = this.limitOfIndex(rangeInfo, rangeInfo.range.length - 2 + index);// * this.chunkLength;
		} else if (index > 2) {
			limit = this.limitOfIndex(rangeInfo, index-3 ); // * this.chunkLength;
		}
		const range = this.indexOf(rangeInfo, index);
		return ((range[0]-limit) / (range[1]-limit))*100;
	}

	rangeStartPercent(rangeInfo: RangeInfo, index: number): number {
		return (this.startOfIndex(rangeInfo, index) / rangeInfo.fileLength)*100;
	}

	rangeEndPercent(rangeInfo: RangeInfo, index: number): number {
		return (this.limitOfIndex(rangeInfo, index)  / rangeInfo.fileLength)*100;
	}
}
