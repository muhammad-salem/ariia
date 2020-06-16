import { Injectable } from '@angular/core';
import { RangeInfo } from '../model/range-info';
import { RangeService } from './range.service';

@Injectable({
  providedIn: 'root'
})
export class RangeInfoService {
  rangeInfo: RangeInfo;
  // chunkLength: number;
  constructor(private rangeService: RangeService) { }

  initRangeInfo(rangeInfo: RangeInfo) {
    this.rangeInfo = rangeInfo;
  }

  indexOf(index: number): number[] {
    return this.rangeService.indexOf(this.rangeInfo, index);
  }

  remainLengthOf(index: number): number {
    return this.rangeService.remainLengthOf(this.rangeInfo, index);
  }

  isFinish(index: number): boolean {
    return this.rangeService.isFinish(this.rangeInfo, index);
  }

  isRangeFinish(): boolean {
    return this.rangeService.isRangeFinish(this.rangeInfo);
  }

  isStreaming(): boolean {
    return this.rangeService.isStreaming(this.rangeInfo);
  }

  hadLength(): boolean {
    return this.rangeService.hadLength(this.rangeInfo);
  }

  getIJ(i: number, j: number): number {
    return this.rangeService.getIJ(this.rangeInfo, i, j);
  }

  startOfIndex(index: number): number {
    return this.rangeService.startOfIndex(this.rangeInfo, index);
  }

  limitOfIndex(index: number): number {
    return this.rangeService.limitOfIndex(this.rangeInfo, index);
  }

  percent(): number {
    return this.rangeService.percent(this.rangeInfo);
  }

  rangePercent(index: number): number {
    return this.rangeService.rangePercent(this.rangeInfo, index);
  }

  rangeStartPercent(index: number): number {
    return this.rangeService.rangeStartPercent(this.rangeInfo, index);
  }

  rangeEndPercent(index: number): number {
    return this.rangeService.rangeEndPercent(this.rangeInfo, index);
  }
}