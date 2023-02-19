import { Service } from '@ibyar/aurora';
import { Observable } from 'rxjs';
import { fromFetch } from 'rxjs/fetch';

@Service({
	provideIn: 'root'
})
export class HttpClient {

	get<T>(url: string): Observable<T> {
        return fromFetch<T>(url, {method: 'GET', selector: s => s.json()});
	}

    post<T>(url: string, body: any): Observable<T> {
        return fromFetch<T>(url, {method: 'POST', body, selector: s => s.json()});
	}
    
	delete<T>(url: string): Observable<T> {
        return fromFetch<T>(url, {method: 'DELETE', selector: s => s.json()});
	}

}

