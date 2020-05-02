
import {Item} from "./item";
import { Message } from './message';
import { NetworkSession } from './network-session';
export class Backbone {

	items: Item[];
	// wattingItem: Item[];
	// downloadingItem: Item[];
	// completeingItem: Item[];
	logging: Message[];

	session: NetworkSession;
	sessionHistory: NetworkSession[];

	constructor(){
		this.items = [];
		// this.wattingItem = [];
		// this.downloadingItem = [];
		// this.completeingItem = [];
		this.logging = [];
		this.session = new NetworkSession();
		this.sessionHistory = [];
	}
}
