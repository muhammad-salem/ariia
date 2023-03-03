
import { Service } from '@ibyar/aurora';
import { Observable } from 'rxjs';
import { HttpClient } from './http.service';

@Service({
	provideIn: 'root'
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
