import { Component, EventEmitter, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService, Item, ItemService, RangeService } from 'core-api';
import { NotifyService } from '../../layout/app-root/notify.service';

@Component({
	selector: 'download-view',
	templateUrl: './download-view.component.html',
	styleUrls: ['./download-view.component.scss']
})
export class DownloadViewComponent implements OnInit {


	item: Item;

	errorMessage: string;

	constructor(private dataService: DataService, private itemService: ItemService,
				private rangeService: RangeService, private notifyService: NotifyService,
				private route: ActivatedRoute, private router: Router) { }

	ngOnInit(): void {
		const id = +this.route.snapshot.params.id;
		this.item = this.dataService.getItem(id);
	}

	deleteItem(): void {
		this.itemService.deleteItem(this.item.id).subscribe(deleted => {
			if (deleted) {
				this.dataService.deleteItem(this.item);
				this.router.navigate(['/download']);
			}
		});
	}

	startItem(): void {
		this.itemService.startItem(this.item.id).subscribe(start => {
			if (start) {
				this.notifyService.info(`Start Download ${this.item.filename}`);
			}
		});
	}

	pauseItem(): void {
		this.itemService.pauseItem(this.item.id).subscribe(pause => {
			if (pause) {
				this.notifyService.info(`Pause Download ${this.item.filename}`);
			}
		});
	}

	downloadPercent(): string {
		return `${this.percent()}%`;
	}

	downloadProgress(): number {
		return +this.percent();
	}

	private percent(): string {
		return this.rangeService.downloadPercent(this.item.rangeInfo).toFixed(2);
	}

}
