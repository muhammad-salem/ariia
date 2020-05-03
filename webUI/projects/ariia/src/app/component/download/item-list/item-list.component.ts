import { Component, OnInit } from '@angular/core';
import { Item } from '../../../model/item';
import { DataService } from '../../../service/data.service';
import { ItemService } from '../../../service/item.service';

@Component({
  selector: 'ariia-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.scss']
})
export class ItemListComponent implements OnInit {

  items: Item[];

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.items = this.dataService.items;
  }

}
