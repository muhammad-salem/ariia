import { Injectable } from '@angular/core';
import { Data } from '../model/data';
import { NetworkSession, NetworkConfig } from '../model/network-session';
import { Message } from '../model/message';
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

  historySubject: BehaviorSubject<void>;
  itemSubject: BehaviorSubject<Item[]>;
  logSubject: BehaviorSubject<Message[]>;

  constructor(private sseService: SseService, private itemService: ItemService) {
    this.data = new Data();
    this.logSubject = new BehaviorSubject<Message[]>(this.data.logging);
    this.itemSubject = new BehaviorSubject<Item[]>(this.data.items);
    this.historySubject = new BehaviorSubject<void>(null);
  }

  get networkSession(): NetworkSession {
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
    if (items) {
      this.data.items.splice(0, this.data.items.length);
      items.forEach(item => this.data.items.push(new Item(item)));
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
      const oldItem = dataService.items.find(searchItem => item['itemId'] === searchItem.id);
      if (oldItem) {
        oldItem.netwotkUpdate(item);
      } else {
        dataService.itemService.getItem(item['itemId']).subscribe((item: Item) => {
          dataService.addItem(item);
          dataService.itemSubject.next(dataService.items);
          // TO:DO
          // show success toast
        });
      }
    });
  }

  initDataService() {
    let date = new Date();
    for (let i = 59; i >= 0; i--) {
      let xDate = new Date();
      xDate.setSeconds(date.getSeconds() - i);
      this.sessionHistory.push({
        x: xDate,
        y: 0,
        session: null
      });
    }


    this.sseService.initSseEvent("/backbone-broadcast").subscribe(() => {
      this.sseService.forEvent("logging").subscribe((messageEvent) => {
        const message: Message = JSON.parse(messageEvent.data);
        message.show = true;
        message.clicked = false;
        message.selected = false;
        this.loggingMessage.splice(0, 0, message);
        if (this.loggingMessage.length > 60) {
          this.loggingMessage.splice(60, this.data.logging.length - 60);
        }
        this.logSubject.next(this.loggingMessage);
      });

      this.sseService.forEvent("event-session").subscribe((messageEvent) => {
        const session: NetworkSession = JSON.parse(messageEvent.data);
        this.networkSession.update(session);
        this.sessionHistory.push({
          x: new Date(),
          y: (session.mointor.tcpDownloadSpeed) / 1024,
          session: session
        });
        if (this.sessionHistory.length > 60) {
          this.sessionHistory.splice(0, this.sessionHistory.length - 60);
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

  initItems() {
    this.itemService.getAllItems().subscribe(items => {
      if (this.items.length > 0) {
        this.items.splice(0, this.items.length);
      }
      this.addItems(items);
      this.itemSubject.next(this.items);
    });
  }

  deleteItem(item: Item) {
    this.items.splice(this.items.indexOf(item), 1);
    this.itemSubject.next(this.items);
  }

  destroy() {
    this.sseService.destroy();
    this.historySubject.unsubscribe();
    this.itemSubject.unsubscribe();
    this.logSubject.unsubscribe();
  }

}