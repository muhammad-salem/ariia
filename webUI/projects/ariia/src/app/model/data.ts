import { Item } from './item';
import { LogMessage } from './log-message';
import { NetworkSession } from './network-session';

export class Data {
    items: Item[];
    logging: LogMessage[];
    session: NetworkSession;
    sessionHistory: NetworkSession[];
    
    constructor(){
        this.items = [];
		this.logging = [];
		this.session = new NetworkSession();
		this.sessionHistory = [];
    }
}
