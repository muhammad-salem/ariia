import { Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import { Item, DataService, RangeService, SessionReport } from 'core-api';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-item-table',
  templateUrl: './item-table.component.html',
  styleUrls: ['./item-table.component.scss']
})
export class ItemTableComponent implements OnInit {

  items: Item[];
  dataSource: MatTableDataSource<Item>;
  session: SessionReport;

  selectedItem: Item;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  columnsToDisplay = [
    'select',
    'filename',
    'fileLength',
    'downloadLength',
    'remainingLength',
    'tcpDownloadSpeed',
    'reminningTime',
    'progress',
    'state'
  ];

  selection: SelectionModel<Item>;

  constructor(private dataService: DataService, private rangeService: RangeService) { }

  ngOnInit(): void {
    this.items = this.dataService.items;
    this.dataSource = new MatTableDataSource(this.items);
    this.dataSource.connect = () => this.dataService.itemSubject;
    this.dataSource.paginator = this.paginator;
    this.session = this.dataService.networkSession;
    const initialSelection = [];
    const allowMultiSelect = true;
    this.selection = new SelectionModel<Item>(allowMultiSelect, initialSelection);
  }
  
  applyFilter(value: string) {
    this.dataSource.filter = value.trim().toLowerCase();
  }

  itemPercent(item: Item): string {
    return `${this.rangeService.percent(item.rangeInfo).toFixed(3)}%`;
  }

  itemProgress(item: Item): number {
    return +this.rangeService.percent(item.rangeInfo).toFixed(3);
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
    //this.dataService.deleteItem(item);
    console.log(this.selection, this.selection.selected);
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.items.length;
    return numSelected == numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.items.forEach(row => this.selection.select(row));
  }

}
