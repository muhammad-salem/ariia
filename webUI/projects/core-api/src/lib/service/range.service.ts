import {Injectable} from '@angular/core';
import {RangeInfo} from '../model/range-info';

@Injectable({
	providedIn: 'root'
})
export class RangeService {

	constructor() {}

	downloadPercent(rangeInfo: RangeInfo): number {
		return (rangeInfo.downloadLength / rangeInfo.fileLength) * 100;
	}

	remainingPercent(rangeInfo: RangeInfo): number {
		return (rangeInfo.remainingLength / rangeInfo.fileLength) * 100;
	}
}
