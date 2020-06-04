import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Properties } from '../model/properties';

@Injectable({
  providedIn: 'root'
})
export class ServerSettingService {

  constructor(private httpClient: HttpClient) { }

  getLevel(): Observable<Properties>  {
    return this.httpClient.get<Properties>('/setting/');
  }

  setLevel(properties: Properties): Observable<boolean> {
    return this.httpClient.post<boolean>('/setting/update', properties);
  }

}
