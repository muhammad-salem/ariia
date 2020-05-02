import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../../service/item.service';
import { Item } from '../../../model/item';
import { BackboneService } from '../../../service/backbone.service';
import { from } from 'rxjs';

@Component({
  selector: 'ariia-download-list',
  templateUrl: './download-list.component.html',
  styleUrls: ['./download-list.component.scss']
})
export class DownloadListComponent implements OnInit {

//  downloadList: Item[];
//  watingList: Item[];
//  completeList: Item[];

  items: Item[];

  constructor(private itemService: ItemService, private backboneService: BackboneService ) {
  }

  ngOnInit(): void {
    this.items = this.backboneService.items;
    this.itemService.getAllItems().subscribe(items => {
      this.backboneService.items = items;
    });
  }

  getItem(url: string){
    //console.log('event', url);
    //this.ngOnInit();
    //this.itemService.getItem(url).subscribe(this.watingList.push);
  }

}
