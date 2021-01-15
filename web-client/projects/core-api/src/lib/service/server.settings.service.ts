import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Properties} from '../model/properties';

@Injectable({
	providedIn: 'root'
})
export class ServerSettingsService {

	constructor(private httpClient: HttpClient) {
	}

	getProperties(): Observable<Properties> {
		return this.httpClient.get<Properties>('/settings/');
	}

	updateProperties(properties: Properties): Observable<boolean> {
		return this.httpClient.post<boolean>('/settings/update', properties);
	}

	isAllowDownload(): Observable<boolean> {
		return this.httpClient.get<boolean>('/settings/isAllowDownload');
	}

	toggleAllowDownload(): Observable<boolean> {
		return this.httpClient.post<boolean>('/settings/toggleAllowDownload', null);
	}


}
