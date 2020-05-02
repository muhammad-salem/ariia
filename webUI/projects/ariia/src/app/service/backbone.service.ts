import { Injectable, NgZone } from '@angular/core';
import { Item } from '../model/item';
import { SseService } from './sse.service';
import { Message } from '../model/message';
import { Backbone } from '../model/backbone';
import { NetworkSession } from '../model/network-session';

@Injectable({
  providedIn: 'root'
})
export class BackboneService {
	
	backbone: Backbone;

  constructor( private _sseService: SseService) { 
    this.backbone = new Backbone();
  }


	get networkSession(): NetworkSession {
		return this.backbone.session;
	}
	
	get loggingMessage(): Message[] {
		return this.backbone.logging;
	}
	
	get items(): Item[] {
		return this.backbone.items;
	}
	
	set items(items: Item[]) {
		if(items){
      this.backbone.items.splice(0, this.backbone.items.length);
      items.forEach(item => this.backbone.items.push(item) );
		}	
	}
	
	addItem(item: Item) {
		this.backbone.items.push(item);
	}

	addItems(items: Item[]) {
		items.forEach(this.addItem);
	}
	

  private update( messageEvent: MessageEvent) {
    const items: Item[] = JSON.parse(messageEvent.data);
    items.forEach(item => {
      const oldItem = this.backbone.items.find((i: Item) => {item.id === i.id;});
      if(oldItem){
        oldItem.state = item.state;
        oldItem.rangeInfo = item.rangeInfo;
      }
    });
  };

  initBackbbone(){

    this._sseService.initSseEvent("/backbone-broadcast").subscribe(()=>{
      this._sseService.forEvent("logging").subscribe((messageEvent)=> {
        this.backbone.logging.push(JSON.parse(messageEvent.data));
      });

      this._sseService.forEvent("event-session").subscribe((messageEvent)=> {
        const session: NetworkSession = JSON.parse(messageEvent.data);
        this.backbone.sessionHistory.push(session);
        this.backbone.session.update(session);

      });

      this._sseService.forEvent("event-item-watting").subscribe(this.update);
      this._sseService.forEvent("event-item-download").subscribe(this.update);
      this._sseService.forEvent("event-item-complete").subscribe(this.update);
    });
  }


}
