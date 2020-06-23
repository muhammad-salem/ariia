import {Component, OnInit} from '@angular/core';
import {ServerSettingsService} from 'core-api';

@Component({
	selector: 'server-control-state',
	templateUrl: './server-control-state.component.html',
	styleUrls: ['./server-control-state.component.scss']
})
export class ServerControlStateComponent implements OnInit {

	listPaused: boolean;
	constructor(private serverSettings: ServerSettingsService) {}

	ngOnInit(): void {
		this.serverSettings.isListPaused()
			.subscribe(paused => this.listPaused = paused);
	}

	toggleServerState(){
		this.serverSettings.isListPaused().subscribe(paused => {
			this.listPaused = paused;
			( this.listPaused ? this.serverSettings.startList() : this.serverSettings.pauseList())
				.subscribe(pausedState => {
					this.listPaused = pausedState;
				});
		});
	}

}
