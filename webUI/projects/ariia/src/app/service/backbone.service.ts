import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';
import { Item } from '../model/item';
import { SseService } from './sse.service';
import { Message } from '../model/message';
import { Backbone } from '../model/backbone';

@Injectable({
  providedIn: 'root'
})
export class BackboneService {
	
	backbone: Backbone;

  constructor( private _sseService: SseService) { 
    this.backbone = new Backbone();
  }

  private update( messageEvent: MessageEvent) {
    const items: Item[] = JSON.parse(messageEvent.data);
    // this.backbone[propName] = items;
    items.forEach(item => {
      const oldItem = this.backbone.items.find((i: Item) => {item.id === i.id;});
      oldItem.state = item.state;
      oldItem.rangeInfo = item.rangeInfo;
    });
  };

  initBackbbone(){
    this._sseService.initSseEvent("/backbone-broadcast").subscribe(()=>{
      this._sseService.subscribe("logging").subscribe((messageEvent)=> {
        this.backbone.logging.push(JSON.parse(messageEvent.data));
      });

      this._sseService.subscribe("event-session").subscribe((messageEvent)=> {
        this.backbone.session = JSON.parse(messageEvent.data);
      });

      this._sseService.subscribe("event-item-watting").subscribe(this.update);
      this._sseService.subscribe("event-item-download").subscribe(this.update);
      this._sseService.subscribe("event-item-complete").subscribe(this.update);

      // function updateCallback(propName: string){
      //   return function update( messageEvent: MessageEvent) {
      //     const items: Item[] = JSON.parse(messageEvent.data);
      //     this.backbone[propName] = items;
      //     items.forEach(item => {
      //       const oldItem = this.backbone.items.find((i: Item) =>{item.id === i.id});
      //       oldItem.state = item.state;
      //       oldItem.rangeInfo = item.rangeInfo;
      //     });
      //   };
      // }

      // this._sseService.subscribe("event-item-watting").subscribe((messageEvent)=> {
      //   const items: Item[] = JSON.parse(messageEvent.data);
      //   this.backbone.wattingItem = items;
      //   items.forEach(item => {
      //     const oldItem = this.backbone.items.find((i)=>{item.id === i.id});
      //     oldItem.state = item.state;
      //     oldItem.rangeInfo = item.rangeInfo;
      //   });
      // });

    });
  }


}
