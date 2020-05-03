import { Injectable } from '@angular/core';
import { RangeInfo } from '../model/range-info';

@Injectable({
  providedIn: 'root'
})
export class RangeInfoService {
	rangeInfo: RangeInfo;
	// chunkLength: number;
	constructor() { }

	initRangeInfo(rangeInfo: RangeInfo) {
		this.rangeInfo = rangeInfo;
		// if (this.rangeInfo.range.length > 2) {
		// 	this.chunkLength = this.limitOfIndex(2);
		// } else {
		// 	this.chunkLength = this.limitOfIndex(1);
		// }
	}

	indexOf(index: number): number[] {
		return this.rangeInfo.range[index];
	}
	/**
	 * 
	 */
	remainLengthOf(index: number): number {
		return this.rangeInfo.range[index][1] - this.rangeInfo.range[index][0];
	}


	private isArrayFinish(ls: number[]): boolean {
		return (ls[1] != -1) && (ls[0] - ls[1] >= 0);
	}
	isFinish(index: number): boolean {
		return this.isArrayFinish(this.indexOf(index));
	}

	isRangeFinish(): boolean {
		for ( var index = 0; index < this.rangeInfo.range.length; index++) {
			if (!this.isFinish(index)){
				return false; // at least one array not yet had finished
			}
		}
		return true;
	}
  
	isStreaming(): boolean {
		return (this.rangeInfo.range.length === 1) && (this.rangeInfo.range[0][0] >= 0);
	}

	hadLength(): boolean {
		return this.rangeInfo.range.length == 1 && this.rangeInfo.range[0][1] == -1;
	}
	
	getIJ(i: number, j: number): number {
		return this.rangeInfo.range[i][j];
	}

	startOfIndex(index: number): number {
		return this.rangeInfo.range[index][0];
	}

	limitOfIndex(index: number): number {
		return this.rangeInfo.range[index][1];
	}

	percent(): number {
		return (this.rangeInfo.downloadLength / this.rangeInfo.fileLength)*100;
	}

	rangePercent(index: number): number {
		let limit = 0;
		if (index < 2) {
			limit = this.limitOfIndex(this.rangeInfo.range.length - 2 + index);// * this.chunkLength;
		} else if (index > 2) {
			limit = this.limitOfIndex(index-3 ); // * this.chunkLength;
		}
		const range = this.indexOf(index);
		return ((range[0]-limit) / (range[1]-limit))*100;
	}

	rangeStartPercent(index: number): number {
		return (this.startOfIndex(index) / this.rangeInfo.fileLength)*100;
	}

	rangeEndPercent(index: number): number {
		return (this.limitOfIndex(index)  / this.rangeInfo.fileLength)*100;
	}
}
