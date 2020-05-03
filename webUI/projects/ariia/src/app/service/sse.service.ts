import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  
  private eventSource: EventSource;
  constructor(private _ngZone: NgZone) { }

  initSseEvent(url: string): Observable<void> {
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

  forEvent(eventName: string): Observable<MessageEvent> {
    return new Observable<MessageEvent>(subscriber => {
      this.eventSource.addEventListener(eventName, (data: MessageEvent)=>{
        this._ngZone.run(()=>{
          subscriber.next(data);
        });
      });
    });
  }
}
