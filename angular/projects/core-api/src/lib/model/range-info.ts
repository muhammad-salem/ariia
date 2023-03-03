export interface RangeInfo {
	fileLength: number;
	downloadLength: number;
	remainingLength: number;
	maxRangePoolNum: number;
	range: number[][];
	finish: boolean;
	streaming: boolean;
}
