import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Item, ItemService, RangeService} from 'core-api';
import {NotifyService} from "../../layout/app-root/notify.service";

@Component({
	selector: 'download-view',
	templateUrl: './download-view.component.html',
	styleUrls: ['./download-view.component.scss']
})
export class DownloadViewComponent implements OnInit {

	@Input() item: Item;
	@Output() delete: EventEmitter<void> = new EventEmitter<void>();

	constructor(private itemService: ItemService, private rangeService: RangeService, private notifyService: NotifyService) {
	}

	ngOnInit(): void {
	}

	deleteItem() {
		return this.itemService.deleteItem(this.item.id).subscribe(deleted => {
			if (deleted) {
				this.delete.emit();
			}
		});
	}

	startItem() {
		return this.itemService.startItem(this.item.id).subscribe(start => {
			if (start) {
				this.notifyService.info(`Start Download ${this.item.filename}`);
			}
		});
	}

	pauseItem() {
		return this.itemService.pauseItem(this.item.id).subscribe(pause => {
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
