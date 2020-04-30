import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SseService {
	
  private eventSource: EventSource;
//   private backboneObservable: Observable<MessageEvent>;

  constructor(private _ngZone: NgZone) { }

  initSseEvent(url: string, ) {
	this.eventSource = new EventSource(url);
	this.eventSource.onerror = console.error;
	this.eventSource.onmessage = console.log;

	return new Observable<void>(subscriber => {
		this.eventSource.onopen = (event) => {
			this._ngZone.run(()=> {
				subscriber.next();
			});
		};
	});
  }

  subscribe(eventName: string){
	return new Observable<MessageEvent>(subscriber => {
		this.eventSource.addEventListener(eventName, (data: MessageEvent)=>{
			this._ngZone.run(()=>{
				subscriber.next(data);
			});
		});
	});
  }

//   private getEventSource(url: string): EventSource {
// 	return new EventSource(url);
//   }
//   initEvent(url: string, ) {
// 	this.backboneObservable = new Observable(subscriber => {
// 		this.eventSource = this.getEventSource(url);
// 		this.eventSource.onerror = error => {
// 			this._ngZone.run(()=>{
// 				subscriber.error(error);
// 			});
// 		};
		
// 		this.eventSource.onopen = event => {
// 			this._ngZone.run(()=>{
// 				subscriber.next(new MessageEvent("open", {
// 					data: "open",
// 				}));
// 			});
// 		};
		
// 		this.eventSource.onmessage = event => {
// 			this._ngZone.run(()=>{
// 				subscriber.next(event);
// 			});
// 		};
		
// 	});
//   }

//   backbone(): Observable<MessageEvent>{
// 	  return this.backboneObservable;
//   }


}
