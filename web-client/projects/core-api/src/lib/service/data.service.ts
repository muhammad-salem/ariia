import {Injectable} from '@angular/core';
import {Data} from '../model/data';
import {NetworkConfig, SessionReport} from '../model/network-session';
import {Message} from '../model/message';
import {assignObject, Item, updateItemFromNetwork} from '../model/item';
import {SseService} from './sse.service';
import {ItemService} from './item.service';
import {SessionHistory} from '../model/session-history';
import {BehaviorSubject} from 'rxjs';
import {SseEventHandler} from '../model/sse-event-handler';

export class NotificationConfig {
	info(title: string, message?: string): void {
		console.log(title, message);
	}

	success(title: string, message?: string): void {
		console.log(title, message);
	}

	error(title: string, message?: string): void {
		console.log(title, message);
	}
}

@Injectable({
	providedIn: 'root'
})
export class DataService {

	historySubject: BehaviorSubject<void>;
	itemSubject: BehaviorSubject<Item[]>;
	logSubject: BehaviorSubject<Message[]>;
	notify: NotificationConfig = new NotificationConfig();
	private data: Data;

	constructor(private sseService: SseService, private itemService: ItemService) {

		this.data = new Data();
		this.logSubject = new BehaviorSubject<Message[]>(this.data.logging);
		this.itemSubject = new BehaviorSubject<Item[]>(this.data.items);
		this.historySubject = new BehaviorSubject<void>(null);
		this.bootstrapChart();
	}

	get networkSession(): SessionReport {
		return this.data.session;
	}

	get sessionHistory(): SessionHistory[] {
		return this.data.sessionHistory;
	}

	get loggingMessage(): Message[] {
		return this.data.logging;
	}

	get networkConfig(): NetworkConfig {
		return this.data.networkConfig;
	}

	set networkConfig(config: NetworkConfig) {
		this.data.networkConfig.isBinary = config.isBinary;
		this.data.networkConfig.isByte = config.isByte;
	}

	get items(): Item[] {
		return this.data.items;
	}

	set items(items: Item[]) {
		if (items && Array.isArray(items)) {
			this.data.items.splice(0, this.data.items.length);
			this.addItems(items);
			this.itemSubject.next(this.items);
		}
	}

	initNotify(notify: NotificationConfig): void {
		this.notify = notify;
	}

	addItem(item: Item) {
		this.data.items.push(item);
	}

	addItems(items: Item[]) {
		items.forEach(item => this.addItem(item));
	}

	initDataService() {
		const events: SseEventHandler[] = [
			{
				name: 'logging',
				handel: (messageEvent) => this.handelLoggingEvent(messageEvent)
			},
			{
				name: 'session-monitor',
				handel: (messageEvent) => this.handelSessionMonitorEvent(messageEvent)
			},
			{
				name: 'item',
				handel: (messageEvent) => this.handelItemEvent(messageEvent)
			},
			{
				name: 'item-list',
				handel: (messageEvent) => this.handelItemListEvent(messageEvent)
			},
			{
				name: 'session-start',
				handel: () => {
					this.initItems();
					this.initDataService();
					this.notify.success('Start Server Successfully');
				}
			},
			{
				name: 'session-shutdown',
				handel: () => {
					this.notify.error('Server got shutdown');
				}
			}
		];

		this.sseService.initSseUrl('/backbone-broadcast', events);
	}

	initItems() {
		this.notify.info('Fetching Download Info', '');
		this.itemService.getAllItems().subscribe(items => {
			if (this.items.length > 0) {
				this.items.splice(0, this.items.length);
			}
			this.addItems(items);
			this.itemSubject.next(this.items);
			this.notify.success('Setup Download Details', `${items.map(item => item.filename)}`);
		});
	}

	deleteItem(item: Item) {
		this.items.splice(this.items.indexOf(item), 1);
		this.itemSubject.next(this.items);
		this.notify.success(item.filename, 'Download Item had been Removed, but the file still');
	}

	destroy() {
		this.sseService.destroy();
		this.historySubject.unsubscribe();
		this.itemSubject.unsubscribe();
		this.logSubject.unsubscribe();
	}

	private bootstrapChart() {
		const date = new Date();
		for (let i = 59; i >= 0; i--) {
			const xDate = new Date();
			xDate.setSeconds(date.getSeconds() - i);
			this.sessionHistory.push({
				x: xDate,
				y: 0,
				session: null
			});
		}
	}

	private handelLoggingEvent(messageEvent: MessageEvent) {
		const message: Message = JSON.parse(messageEvent.data);
		this.loggingMessage.push(message);
		if (this.loggingMessage.length > 60) {
			this.loggingMessage.splice(0, this.loggingMessage.length - 60);
		}
		this.logSubject.next(this.loggingMessage);
	}

	private handelSessionMonitorEvent(messageEvent: MessageEvent): void {
		const session: SessionReport = JSON.parse(messageEvent.data);
		assignObject(this.networkSession, session);
		this.sessionHistory.push({
			x: new Date(),
			y: (session.monitor.tcpDownloadSpeed) / 1024,
			session
		});
		if (this.sessionHistory.length > 60) {
			this.sessionHistory.splice(0, this.sessionHistory.length - 60);
		}
		this.historySubject.next();
	}

	private handelItem(item: Item) {
		const oldItem = this.items.find(searchItem => item.uuid === searchItem.uuid);
		if (oldItem) {
			updateItemFromNetwork(oldItem, item);
		} else {
			this.itemService.getItem(item.id).subscribe((newItem: Item) => {
				this.addItem(newItem);
				updateItemFromNetwork(newItem, item);
				this.itemSubject.next(this.items);
				this.notify.success(newItem.filename, `${newItem.url}<br>${newItem.state}`);
			});
		}
	}

	private handelItemEvent(messageEvent: MessageEvent) {
		const item: Item = JSON.parse(messageEvent.data);
		this.handelItem(item);
	}

	private handelItemListEvent(messageEvent: MessageEvent) {
		const items: Item[] = JSON.parse(messageEvent.data);
		items.forEach(item => this.handelItem(item));
	}

}
