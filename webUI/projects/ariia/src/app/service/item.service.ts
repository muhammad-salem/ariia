import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Item } from '../model/item';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(private httpClient: HttpClient) { }

  getItem(id: string): Observable<Item>  {
    return this.httpClient.get<Item>('/items/' + id);
  }

  getAllItems(): Observable<Item[]>  {
    return this.httpClient.get<Item[]>('/items/all');
  }

  downloadUrl(url: string): Observable<string> {
    return this.httpClient.post<string>('/items/createUrl', url);
  }

  downloadItem(item: Item): Observable<string> {
    return this.httpClient.post<string>('/items/createItem', item);
  }

}
