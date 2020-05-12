import { Item } from './item';
import { LogMessage } from './log-message';
import { NetworkSession } from './network-session';
import { SessionHistory } from './session-history';

export class Data {
    items: Item[];
    logging: LogMessage[];
    session: NetworkSession;
    sessionHistory: SessionHistory[];
    
    constructor(){
        this.items = [];
		this.logging = [];
		this.session = new NetworkSession();
		this.sessionHistory = [];
    }
}
