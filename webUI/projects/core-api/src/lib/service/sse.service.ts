import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {SseEventHandler} from '../model/sse-event-handler';


@Injectable({
	providedIn: 'root'
})
export class SseService {

	private eventSource: EventSource;

	constructor() {
	}

	initSseUrl(url: string, events: SseEventHandler[]): void {
		this.eventSource = new EventSource(url);
		this.eventSource.onerror = console.error;
		this.eventSource.onmessage = console.log;
		this.addEventsListener(events);
	}

	addEventListener(event: SseEventHandler): void {
		this.eventSource.addEventListener(event.name,
			(data: MessageEvent) => {
				event.handel(data);
			}
		);
	}

	addEventsListener(events: SseEventHandler[]): void {
		events.forEach(event => {
			this.addEventListener(event);
		});
	}

	listen(eventName: string, handler?: any) {
		this.addEventListener({
			name: eventName,
			handel: (data) => handler(data)
		});
	}

	getListener(eventName: string): Observable<MessageEvent> {
		return new Observable<MessageEvent>(subscriber => {
			this.eventSource.addEventListener(eventName, (data: MessageEvent) => {
				subscriber.next(data);
			});
		});
	}

	destroy() {
		this.eventSource.close();
	}
}
