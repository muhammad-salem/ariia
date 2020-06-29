import {Component, OnInit} from '@angular/core';
import {DataService} from 'core-api';
import {NotifyService} from "./notify.service";


@Component({
	selector: 'app-root',
	templateUrl: './app-root.component.html',
	styleUrls: ['./app-root.component.scss']
})
export class AppRootComponent implements OnInit {

	sideBarOpen = true;

	constructor(private dataService: DataService, private notifyService: NotifyService) {}

	ngOnInit(): void {
		this.dataService.initNotify(this.notifyService);
		this.dataService.initItems();
		this.dataService.initDataService();
		window.onbeforeunload = () => {
			this.dataService.destroy();
		};
	}

}
