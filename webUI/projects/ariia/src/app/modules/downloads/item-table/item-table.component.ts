import { Component, OnInit } from '@angular/core';
import { Item, DataService, RangeService, NetworkSession } from 'core-api';
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
  session: NetworkSession;

  selectedItem: Item;

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
    this.session = this.dataService.networkSession;
    const initialSelection = [];
    const allowMultiSelect = true;
    this.selection = new SelectionModel<Item>(allowMultiSelect, initialSelection);
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

  onItemDelete(item: Item) {
    this.dataService.deleteItem(item);
  }

  selectItem(item: Item) {
    this.selectedItem = item;
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
