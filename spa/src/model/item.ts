import { RangeInfo } from './range-info';
import { RangeReport } from './network-session';

export interface Item {
	id: number;
	uuid: string;
	url: string;
	redirectUrl: string;
	mirrors: string[];
	filename: string;
	state: string;
	saveDirectory: string;
	headers: Map<string, string[]>;
	rangeInfo: RangeInfo;
	report: RangeReport;
}

export const assignObject = (object: Record<PropertyKey, any>, copy: Record<PropertyKey, any>) => {
	Object.keys(copy).forEach(key => object[key] = copy[key]);
};

export const updateItemFromNetwork = (item: Item, networkItem: Item) => {
	item.id = networkItem.id;
	item.state = networkItem.state;
	item.report = networkItem.report;
	assignObject(item.rangeInfo, networkItem.rangeInfo);
};

export const updateItem = (item1: Item, item2: Item) => {
	assignObject(item1, item2);
};
