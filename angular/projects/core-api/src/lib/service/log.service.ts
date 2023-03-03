import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class LogService {

	constructor(private httpClient: HttpClient) {
	}

	getLevel(): Observable<string> {
		return this.httpClient.get<string>('/logging/');
	}

	levelValues(): Observable<string[]> {
		return this.httpClient.get<string[]>('/logging/values');
	}

	setLevel(levelName: string): Observable<boolean> {
		return this.httpClient.post<boolean>('/logging/set', levelName);
	}

}
