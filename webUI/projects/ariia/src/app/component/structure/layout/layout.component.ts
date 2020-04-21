import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../../service/item.service';
import { Item } from '../../../model/item';

@Component({
  selector: 'ariia-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
