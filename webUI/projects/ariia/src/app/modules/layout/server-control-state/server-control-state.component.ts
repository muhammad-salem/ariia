import {Component, OnInit} from '@angular/core';
import {ServerSettingsService} from 'core-api';

@Component({
	selector: 'server-control-state',
	templateUrl: './server-control-state.component.html',
	styleUrls: ['./server-control-state.component.scss']
})
export class ServerControlStateComponent implements OnInit {

	allowDownload: boolean;

	constructor(private serverSettings: ServerSettingsService) {
	}

	ngOnInit(): void {
		this.serverSettings.isAllowDownload()
			.subscribe(allow => this.allowDownload = allow);
	}

	toggleServerState() {
		this.serverSettings.toggleAllowDownload()
			.subscribe(allow => this.allowDownload = allow);
	}

}
