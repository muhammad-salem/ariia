import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService, Item, ItemService, RangeService } from 'core-api';
import { NotifyService } from '../../layout/app-root/notify.service';
import { DownloadEditComponent } from '../download-edit/download-edit.component';

@Component({
	selector: 'download-view',
	templateUrl: './download-view.component.html',
	styleUrls: ['./download-view.component.scss']
})
export class DownloadViewComponent implements OnInit {


	item: Item;

	errorMessage: string;

	constructor(
		private dataService: DataService,
		private itemService: ItemService,
		private rangeService: RangeService,
		private notifyService: NotifyService,
		private dialog: MatDialog,
		private route: ActivatedRoute,
		private router: Router) { }

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

	editItem(): void {
		const dialogRef = this.dialog.open(DownloadEditComponent, {
			data: {
				url: this.item.url,
				filename: this.item.filename,
				saveDirectory: this.item.saveDirectory,
				headers: this.item.headers,
				redirectUrl: this.item.redirectUrl,
			},
		});
		dialogRef.afterClosed().subscribe((item: Item) => this.itemService.updateItem(this.item.id, item).subscribe());
	}

	downloadPercent(): string {
		return `${this.percent()}%`;
	}

	downloadProgress(): number {
		return +this.percent();
	}

	percent(): string {
		return this.rangeService.downloadPercent(this.item.rangeInfo).toFixed(2);
	}

	svgFill(index: number): string {
		return this.rangeService.isFinish(this.item.rangeInfo, index) ? 'green' : 'blue';
	}

	getX(index: number): number {
		return (index % 50) * 15;
	}

	getY(index: number): number {
		return ~~(index / 50) * 15;
	}

	getSvgHeight(): number {
		const rowNum = ~~(this.item?.rangeInfo?.range?.length / 50) + (this.item?.rangeInfo?.range?.length % 50 > 0 ? 1 : 0);
		return rowNum * 15;
	}

}
