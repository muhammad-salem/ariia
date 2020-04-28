import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Item } from '../model/item';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(private httpClient: HttpClient) { }

  getAllItems(): Observable<Item[]>  {
    return this.httpClient.get<Item[]>('/items');
  }

  getItem(id: string): Observable<Item>  {
    return this.httpClient.get<Item>('/items/info/' + id);
  }

  downloadUrl(url: string): Observable<string> {
    return this.httpClient.post<string>('/items/create/url', url);
  }

  downloadItem(item: Item): Observable<string> {
    return this.httpClient.post<string>('/items/create/url', item);
  }

}
