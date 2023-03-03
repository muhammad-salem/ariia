import {Component, OnInit} from '@angular/core';
import { DataService, SessionReport } from 'core-api';

@Component({
	selector: 'app-network-viewer',
	templateUrl: './network-viewer.component.html',
	styleUrls: ['./network-viewer.component.scss']
})
export class NetworkViewerComponent implements OnInit {

	session: SessionReport;

	constructor(private dataService: DataService) {}

	ngOnInit(): void {
		this.session = this.dataService.networkSession;
	}

	sessionProgress(): number {
		if (this.session.totalLength) {
			return +(((this.session.downloadLength / this.session.totalLength) * 100).toFixed(2));
		}
		return 100;
	}

}
