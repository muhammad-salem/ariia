import { Injectable } from '@angular/core';
import { Data } from '../model/data';
import { NetworkConfig, SessionReport } from '../model/network-session';
import { Message } from '../model/message';
import { Item } from '../model/item';
import { SseService } from './sse.service';
import { ItemService } from './item.service';
import { SessionHistory } from '../model/session-history';
import { BehaviorSubject } from 'rxjs';
import { SseEventHandler } from '../model/sse-event-handler';

export class NotificationConfig {
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

  private data: Data;

  historySubject: BehaviorSubject<void>;
  itemSubject: BehaviorSubject<Item[]>;
  logSubject: BehaviorSubject<Message[]>;

  notify: NotificationConfig = new NotificationConfig();

  constructor(private sseService: SseService, private itemService: ItemService) {

    this.data = new Data();
    this.logSubject = new BehaviorSubject<Message[]>(this.data.logging);
    this.itemSubject = new BehaviorSubject<Item[]>(this.data.items);
    this.historySubject = new BehaviorSubject<void>(null);
    this.bootstrapChart();
  }

  initNotify(notify: NotificationConfig): void {
    this.notify = notify;
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

  // private update(dataService: DataService, messageEvent: MessageEvent) {
  //   const items: Item[] = JSON.parse(messageEvent.data);
  //   items.forEach(item => {
  //     const oldItem = dataService.items.find(searchItem => item['itemId'] === searchItem.id);
  //     if (oldItem) {
  //       oldItem.netwotkUpdate(item);
  //     } else {
  //       dataService.itemService.getItem(item['itemId']).subscribe((item: Item) => {
  //         dataService.addItem(item);
  //         dataService.itemSubject.next(dataService.items);
  //         // TO:DO
  //         // show success toast
  //         this.notify.success(item.filename, `${item.url} - ${item.state}`);
  //       });
  //     }
  //   });
  // }

  private bootstrapChart() {
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
  }

  private handelLoggingEvent(messageEvent: MessageEvent) {
    const message: Message = JSON.parse(messageEvent.data);
    this.loggingMessage.push(message);
    this.logSubject.next(this.loggingMessage);
  }

  private handelSessionMonitorEvent(messageEvent: MessageEvent): void {
    const session: SessionReport = JSON.parse(messageEvent.data);
    this.networkSession.update(session);
    this.sessionHistory.push({
      x: new Date(),
      y: (session.monitor.tcpDownloadSpeed) / 1024,
      session: session
    });
    if (this.sessionHistory.length > 60) {
      this.sessionHistory.splice(0, this.sessionHistory.length - 60);
    }
    this.historySubject.next();
  }

  private handelItem(item: Item) {
    const oldItem = this.items.find(searchItem => item['itemId'] === searchItem.id);
    if (oldItem) {
      oldItem.netwotkUpdate(item);
    } else {
      this.itemService.getItem(item['itemId']).subscribe((item: Item) => {
        this.addItem(item);
        this.itemSubject.next(this.items);
        // TO:DO
        // show success toast
        this.notify.success(item.filename, `${item.url} - ${item.state}`);
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
        handel: (messageEvent) => {
          this.initItems();
          this.initDataService();
          this.notify.success('Start Server Successfuly');
        }
      },
      {
        name: 'session-shutdown',
        handel: (messageEvent) => {
          this.notify.error('Server got shutdown');
        }
      }
    ];

    this.sseService.initSseUrl('/backbone-broadcast', events);
    // this.sseService.initSseEvent("/backbone-broadcast").subscribe(() => {
    //   this.sseService.forEvent("logging").subscribe((messageEvent) => {
    //     const message: Message = JSON.parse(messageEvent.data);
    //     // this.loggingMessage.splice(0, 0, message);
    //     // if (this.loggingMessage.length > 60) {
    //     //   this.loggingMessage.splice(60, this.data.logging.length - 60);
    //     // }
    //     this.loggingMessage.push(message);
    //     this.logSubject.next(this.loggingMessage);
    //   });

    //   this.sseService.forEvent('session-monitor').subscribe((messageEvent) => {
    //     const session: SessionReport = JSON.parse(messageEvent.data);
    //     this.networkSession.update(session);
    //     this.sessionHistory.push({
    //       x: new Date(),
    //       y: (session.monitor.tcpDownloadSpeed) / 1024,
    //       session: session
    //     });
    //     if (this.sessionHistory.length > 60) {
    //       this.sessionHistory.splice(0, this.sessionHistory.length - 60);
    //     }
    //     this.historySubject.next();
    //   });

    //   this.sseService.forEvent('item-list')
    //     .subscribe(messageEvent => this.update(this, messageEvent));

    //   this.sseService.forEvent('item')
    //     .subscribe(messageEvent => this.update(this, messageEvent));

    //   this.sseService.forEvent("new-session").subscribe(() => {
    //     // show success toast >> that server restarted
    //     this.initItems();
    //     this.initDataService();
    //     this.notify.success('Start Server Successfuly');
    //   });

    //   this.sseService.forEvent("shutdown").subscribe(() => {
    //     // this.sseService.destroy();
    //     // show warning toast >> that server had to shutdown
    //     this.notify.error('Server got shutdown');
    //   });
    // });

  }

  initItems() {
    this.itemService.getAllItems().subscribe(items => {
      if (this.items.length > 0) {
        this.items.splice(0, this.items.length);
      }
      this.addItems(items);
      this.itemSubject.next(this.items);
      this.notify.success('Load Items', `${items.map(item => item.filename).join('\n')}`);
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

}
