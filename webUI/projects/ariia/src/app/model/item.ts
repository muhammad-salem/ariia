import {RangeInfo} from "./range-info";
export class Item {
  id: string;
  url: string;
  redirectUrl: string;
  filename: string;
  state: string;
  saveDirectory: string;
  headers: Map<string, string[]>;
  rangeInfo: RangeInfo;
}
