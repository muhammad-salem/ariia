import { Item } from './item';
import { Message } from './message';
import { NetworkSession, NetworkConfig } from './network-session';
import { SessionHistory } from './session-history';

export class Data {
    items: Item[];
    logging: Message[];
    session: NetworkSession;
    sessionHistory: SessionHistory[];
    networkConfig: NetworkConfig;

    constructor() {
        this.items = [];
        this.logging = [];
        this.session = new NetworkSession();
        this.sessionHistory = [];
        this.networkConfig = { isBinary: true, isByte: true };
    }
}