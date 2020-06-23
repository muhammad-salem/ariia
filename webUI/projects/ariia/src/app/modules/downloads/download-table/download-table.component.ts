import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {DataService, Item, RangeService, SessionReport} from 'core-api';
import {SelectionModel} from '@angular/cdk/collections';
import {MatTable, MatTableDataSource} from '@angular/material/table';
import {MatSort, Sort} from '@angular/material/sort';

@Component({
	selector: 'download-table',
	templateUrl: './download-table.component.html',
	styleUrls: ['./download-table.component.scss']
})
export class DownloadTableComponent implements OnInit {

	items: Item[];
	dataSource: MatTableDataSource<Item>;
	session: SessionReport;

	@ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
	@ViewChild(MatTable, {static: true}) table: MatTable<Item>;

	columnsToDisplay = [
		'select',
		'filename',
		'percent',
		'downloadLength',
		'fileLength',
		'remainingLength',
		'tcpDownloadSpeed',
		'remainingTime',
		// 'progress',
		'state'
	];

	selection: SelectionModel<Item>;

	constructor(private dataService: DataService, private rangeService: RangeService) {
	}

	ngOnInit(): void {
		this.session = this.dataService.networkSession;
		this.items = this.dataService.items;
		this.dataSource = new MatTableDataSource(this.items);
		this.dataSource.connect = () => this.dataService.itemSubject;
		this.dataSource.filterPredicate = (item: Item, filter: string) => {
			// for (const key in Object.keys(item)) {
			//   // if (item[key])
			// }
			console.log(filter, item);
			this.table.renderRows();
			return true;
		};
		this.dataSource.sortData = (data: Item[], matSort: MatSort) => {
			// const result: Item[] = [];
			const result = data.sort((e1: Item, e2: Item) => {
				const isAsc = matSort.direction === 'asc';
				switch (matSort.active) {
					case 'filename': return compare(e1.filename, e2.filename, isAsc);
					case 'percent': return compare(this.downloadPercent(e1), this.downloadPercent(e2), isAsc);
					case 'fileLength': return compare(e1.rangeInfo.fileLength, e2.rangeInfo.fileLength, isAsc);
					case 'downloadLength': return compare(e1.rangeInfo.downloadLength, e2.rangeInfo.downloadLength, isAsc);
					case 'remainingLength': return compare(e1.rangeInfo.remainingLength, e2.rangeInfo.remainingLength, isAsc);
					case 'tcpDownloadSpeed': return compare(e1.report.monitor.tcpDownloadSpeed, e2.report.monitor.tcpDownloadSpeed, isAsc);
					case 'remainingTime': return compare(e1.report.remainingTime, e2.report.remainingTime, isAsc);
					case 'state': return compare(e1.state, e2.state, isAsc);
					default : return 0;
				}
			});
			return result;
		};
		this.dataSource.paginator = this.paginator;
		this.selection = new SelectionModel<Item>(false, []);
	}

	applyFilter(value: string) {
		this.dataSource.filter = value.trim().toLowerCase();
		this.table.renderRows();
	}

	private downloadPercent(item: Item): number {
		return this.rangeService.downloadPercent(item.rangeInfo);
	}

	itemPercent(item: Item): string {
		const percent = this.downloadPercent(item);
		if (percent === 100 || percent === 0) {
			return `${percent}%`;
		}
		return `${percent.toFixed(2)}%`;
	}

	itemProgress(item: Item): number {
		return +this.downloadPercent(item).toFixed(2);
	}

	sessionProgress(): number {
		if (this.session.totalLength) {
			return +(((this.session.downloadLength / this.session.totalLength) * 100).toFixed(2));
		}
		return 100;
	}

	startItem() {
		console.log(this.selection, this.selection.selected);
		/*
		return this.itemService.startItem(this.item.id).subscribe(start => {
		  if (start) {
			console.info(`start download item ${this.item}`);
		  }
		});
		*/
	}

	pauseItem() {
		console.log(this.selection, this.selection.selected);
		/*
		return this.itemService.pauseItem(this.item.id).subscribe(pause => {
		  if (pause) {
			console.info(`pause download item ${this.item}`);
		  }
		});
		*/
	}

	deleteItem() {
		// this.dataService.deleteItem(item);
		console.log(this.selection, this.selection.selected);
	}

	/** Whether the number of selected elements matches the total number of rows. */
	isAllSelected() {
		const numSelected = this.selection.selected.length;
		const numRows = this.items.length;
		return numSelected === numRows;
	}

	/** Selects all rows if they are not all selected; otherwise clear selection. */
	masterToggle() {
		this.isAllSelected() ?
			this.selection.clear() :
			this.items.forEach(row => this.selection.select(row));
	}

}

function compare(a: number | string, b: number | string, isAsc: boolean) {
	return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
