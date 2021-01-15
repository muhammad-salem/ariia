import {Item} from './item';
import {Message} from './message';
import {NetworkConfig, SessionReport, SimpleSessionReport} from './network-session';
import {SessionHistory} from './session-history';

export class Data {
	items: Item[];
	logging: Message[];
	session: SessionReport;
	sessionHistory: SessionHistory[];
	networkConfig: NetworkConfig;

	constructor() {
		this.items = [];
		this.logging = [];
		this.session = new SimpleSessionReport();
		this.sessionHistory = [];
		this.networkConfig = {isBinary: true, isByte: true};
	}
}
