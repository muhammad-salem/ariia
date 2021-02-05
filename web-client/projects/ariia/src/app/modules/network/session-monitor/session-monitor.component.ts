import { Component, OnInit } from '@angular/core';
import { DataService, SessionReport } from 'core-api';

@Component({
	selector: 'session-monitor',
	templateUrl: './session-monitor.component.html',
	styleUrls: ['./session-monitor.component.scss']
})
export class SessionMonitorComponent implements OnInit {

	session: SessionReport;
	isBinary = true;

	constructor(private dataService: DataService) {}

	ngOnInit(): void {
		this.session = this.dataService.networkSession;
	}

	sessionPercent(): string {
		if (this.session.totalLength) {
			return `${((this.session.downloadLength / this.session.totalLength) * 100).toFixed(2)}%`;
		}
		return `100%`;
	}

	sessionProgress(): number {
		if (this.session.totalLength) {
			return +(((this.session.downloadLength / this.session.totalLength) * 100).toFixed(2));
		}
		return 100;
	}
}
