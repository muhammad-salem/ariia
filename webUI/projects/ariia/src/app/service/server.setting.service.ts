import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Properties } from '../model/properties';

@Injectable({
  providedIn: 'root'
})
export class ServerSettingService {

  constructor(private httpClient: HttpClient) { }

  getProperties(): Observable<Properties>  {
    return this.httpClient.get<Properties>('/setting/');
  }

  updateProperties(properties: Properties): Observable<boolean> {
    return this.httpClient.post<boolean>('/setting/update', properties);
  }

}
