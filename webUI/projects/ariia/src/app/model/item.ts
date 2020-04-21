
export class RangeInfo {
  fileLength: number;
  downloadLength: number;
  remainingLength: number;
  maxRangePoolNum: number;
  range: number[][];
}

export class Item {

  id: string;
  url: string;
  filename: string;
  saveDirectory: string;
  headers: Map<string, string[]>;
  rangeInfo: RangeInfo;
}
