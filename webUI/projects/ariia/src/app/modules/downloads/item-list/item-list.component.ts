import { Component, OnInit } from '@angular/core';
import { Item, DataService } from 'core-api';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {

  items: Item[];

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.items = this.dataService.items;
  }

  onItemDelete(item: Item) {
    this.dataService.deleteItem(item);
  }

}
