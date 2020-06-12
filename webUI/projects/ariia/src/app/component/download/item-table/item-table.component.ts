import { Component, OnInit } from '@angular/core';
import { Item } from '../../../model/item';
import { DataService } from '../../../service/data.service';
import { ItemService } from '../../../service/item.service';
import { RangeService } from '../../../service/range.service';

@Component({
  selector: 'ariia-item-table',
  templateUrl: './item-table.component.html',
  styleUrls: ['./item-table.component.scss']
})
export class ItemTableComponent implements OnInit {

  items: Item[];
  isBinary: boolean = true;
  
  selectedItem: Item;
  
  constructor(private dataService: DataService, private rangeService: RangeService) { }

  ngOnInit(): void {
    this.items = this.dataService.items;
  }
  
  itemPercent(item: Item): string {
    return `${this.rangeService.percent(item.rangeInfo).toFixed(3)}%`;
  }

  onItemDelete(item: Item) {
    this.dataService.deleteItem(item);
  }
  
  selectItem(item: Item) {
    this.selectedItem = item;
  }

}
