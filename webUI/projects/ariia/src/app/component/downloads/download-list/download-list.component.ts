import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../../service/item.service';
import { Item } from '../../../model/item';

@Component({
  selector: 'ariia-download-list',
  templateUrl: './download-list.component.html',
  styleUrls: ['./download-list.component.scss']
})
export class DownloadListComponent implements OnInit {

  downloadList: Item[];
  watingList: 	Item[];
  completeList: Item[];

  constructor(private itemService: ItemService) {
    this.initLists();
   }
   
   initLists() {
   	this.downloadList = [];
    this.watingList   = [];
    this.completeList = [];
   }

  ngOnInit(): void {
    this.itemService.getAllItems().subscribe(items => {
      console.log(items);
      this.initLists();
      items.forEach(item => {
        if (item.rangeInfo.remainingLength == 0) {
          this.downloadList.push(item);
        } else {
          this.watingList.push(item);
        }
      });
      console.log(this.downloadList);
      console.log(this.watingList);
    });
  }

  getItem(url: string){
    //console.log('event', url);
    this.ngOnInit();
    //this.itemService.getItem(url).subscribe(this.watingList.push);
  }

}
