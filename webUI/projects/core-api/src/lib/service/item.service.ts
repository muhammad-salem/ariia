import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Item} from '../model/item';

@Injectable({
	providedIn: 'root'
})
export class ItemService {

	constructor(private httpClient: HttpClient) {
	}

	getAllItems(): Observable<Item[]> {
		return this.httpClient.get<Item[]>('/items');
	}

	getItem(id: number): Observable<Item> {
		return this.httpClient.get<Item>('/items/info/' + id);
	}

	downloadUrl(url: string): Observable<number> {
		return this.httpClient.post<number>('/items/create/url', url);
	}

	downloadItem(item: Item): Observable<number> {
		return this.httpClient.post<number>('/items/create/url', item);
	}

	deleteItem(id: number): Observable<boolean> {
		return this.httpClient.delete<boolean>('/items/delete/' + id);
	}

	startItem(id: number): Observable<boolean> {
		return this.httpClient.post<boolean>('/items/start/' + id, null);
	}

	pauseItem(id: number): Observable<boolean> {
		return this.httpClient.post<boolean>('/items/pause/' + id, null);
	}

	downloadFile(id: number): Observable<any> {
		return this.httpClient.get<any>('/items/download/' + id);
	}

	createMetaLinkUrl(urls: string[]): Observable<number> {
		return this.httpClient.post<number>('/items/create/metaLink', urls);
	}

	createMetaLinkItem(item: Item): Observable<number> {
		return this.httpClient.post<number>('/items/create/metaLinkItem', item);
	}

}
