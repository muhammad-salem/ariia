import { Injectable } from '@angular/core';
import { Data } from '../model/data';
import { NetworkSession } from '../model/network-session';
import { LogMessage } from '../model/log-message';
import { Item } from '../model/item';
import { SseService } from './sse.service';
import { ItemService } from './item.service';
import { SessionHistory } from '../model/session-history';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

	private data: Data;

	historySubject: BehaviorSubject<void> = new BehaviorSubject<void>(null);

	constructor(private sseService: SseService, private itemService: ItemService,) { 
		this.data = new Data();
	}

	get networkSession(): NetworkSession {
		return this.data.session;
	}

	get sessionHistory(): SessionHistory[] {
		return this.data.sessionHistory;
	}

	get loggingMessage(): LogMessage[] {
		return this.data.logging;
	}

	get items(): Item[] {
		return this.data.items;
	}

	set items(items: Item[]) {
		if(items){
			this.data.items.splice(0, this.data.items.length);
			items.forEach(item => this.data.items.push(new Item(item)) );
		}	
	}

	addItem(item: Item) {
		this.data.items.push(new Item(item));
	}

	addItems(items: Item[]) {
		items.forEach(item => {
			this.addItem(item);
		});
	}
  
	private update(dataService: DataService, messageEvent: MessageEvent) {
		const items: Item[] = JSON.parse(messageEvent.data);
		items.forEach(item => {
		  const oldItem = dataService.data.items.find(i => item['itemId'] === i.id);
		  if(oldItem){
			oldItem.state = item.state;
			oldItem.rangeInfo.update(item.rangeInfo);
		  } else {
			dataService.itemService.getItem(item['itemId']).subscribe(dataService.data.items.push);
		  }
		});
	}

	initDataService() {
		let date = new Date();
		for (let i = 59; i >= 0 ; i--) {
			let xDate = new Date();
			xDate.setSeconds(date.getSeconds() - i);
			this.data.sessionHistory.push({
				x: xDate,
				y: 0, 
				session: null
			});
		}


		this.sseService.initSseEvent("/backbone-broadcast").subscribe(()=> {
		  this.sseService.forEvent("logging").subscribe((messageEvent)=> {
			this.data.logging.push(JSON.parse(messageEvent.data));
			if(this.data.logging.length > 60){
				this.data.logging.splice(0, this.data.logging.length-60);
			}
		  });

		  this.sseService.forEvent("event-session").subscribe((messageEvent)=> {
			const session: NetworkSession = JSON.parse(messageEvent.data);
			this.data.session.update(session);
			this.data.sessionHistory.push({
				x: new Date(),
				y: (session.receiveTCP - session.receiveTCP_old) / 1024, 
				session: session 
			});
			if(this.data.sessionHistory.length > 60){
				this.data.sessionHistory.splice(0, this.data.sessionHistory.length-60);
			}
			this.historySubject.next();
		  });

		  this.sseService.forEvent("event-item-watting")
				  .subscribe(messageEvent => this.update(this, messageEvent));
		  this.sseService.forEvent("event-item-download")
				  .subscribe(messageEvent => this.update(this, messageEvent));
		  this.sseService.forEvent("event-item-complete")
				  .subscribe(messageEvent => this.update(this, messageEvent));
		});

	}

	initItems(){
		this.itemService.getAllItems().subscribe(items => {
			this.items.splice(0, this.items.length);
			this.addItems(items);
		});
	}

	deleteItem(item: Item){
		this.items.splice(this.items.indexOf(item), 1);
	}

	destroy(){
		this.sseService.destroy();
	}

}
