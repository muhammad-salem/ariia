import { Component, OnInit, Input } from '@angular/core';
import { Item } from '../../../model/item';
import { ItemService } from '../../../service/item.service';

@Component({
  selector: 'ariia-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss']
})
export class ItemComponent implements OnInit {

  @Input() item: Item;

  constructor(private itemService: ItemService) {
    this.item = new Item();
   }

  ngOnInit(): void {
    console.log('item', this.item);
    // this.itemService.getItem('3def64bf-2046-47a1-ab8a-dd0412564674').subscribe(itemData => {
    //   this.item = itemData;
    // });
  }

}
