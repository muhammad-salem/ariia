import {Component, OnInit} from '@angular/core';
import {DataService, Item} from 'core-api';

@Component({
	selector: 'download-list',
	templateUrl: './download-list.component.html',
	styleUrls: ['./download-list.component.scss']
})
export class DownloadListComponent implements OnInit {

	items: Item[];

	constructor(private dataService: DataService) {
	}

	ngOnInit(): void {
		this.items = this.dataService.items;
	}

	onItemDelete(item: Item) {
		this.dataService.deleteItem(item);
	}

}
