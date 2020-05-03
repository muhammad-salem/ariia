import { Injectable } from '@angular/core';
import { Data } from '../model/data';
import { NetworkSession } from '../model/network-session';
import { LogMessage } from '../model/log-message';
import { Item } from '../model/item';
import { SseService } from './sse.service';
import { ItemService } from './item.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private data: Data;
  constructor(private sseService: SseService, private itemService: ItemService,) { 
    this.data = new Data();
  }

  get networkSession(): NetworkSession {
		return this.data.session;
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
		items.forEach(this.addItem);
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

    this.sseService.initSseEvent("/backbone-broadcast").subscribe(()=> {
      this.sseService.forEvent("logging").subscribe((messageEvent)=> {
        this.data.logging.push(JSON.parse(messageEvent.data));
      });

      this.sseService.forEvent("event-session").subscribe((messageEvent)=> {
        const session: NetworkSession = JSON.parse(messageEvent.data);
        this.data.sessionHistory.push(session);
        this.data.session.update(session);
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
      this.items = items;
    });
  }

}
