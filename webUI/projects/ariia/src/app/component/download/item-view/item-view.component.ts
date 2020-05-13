import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Item } from '../../../model/item';
import { RangeInfoService } from '../../../service/range-info.service';
import { ItemService } from '../../../service/item.service';
import { faTrash, faDownload, faPauseCircle, faCloudDownloadAlt } from '@fortawesome/free-solid-svg-icons';


@Component({
  selector: 'ariia-item-view',
  templateUrl: './item-view.component.html',
  styleUrls: ['./item-view.component.scss'],
  providers: [
    {provide: RangeInfoService}
  ]
})
export class ItemViewComponent implements OnInit {

  @Input() item: Item;
  @Output() delete: EventEmitter<void> = new EventEmitter<void>();

  faTrash = faTrash;
  faDownload = faDownload;
  faPauseCircle = faPauseCircle;
  faCloudDownloadAlt = faCloudDownloadAlt;
  

  constructor(private rangeInfoService: RangeInfoService, private itemService: ItemService) { }

  ngOnInit(): void {
    this.rangeInfoService.initRangeInfo(this.item.rangeInfo);
  }

  deleteItem(){
    return this.itemService.deleteItem(this.item.id).subscribe(deleted => {
      if (deleted) {
        this.delete.emit();
      }
    });
  }
  
  startItem(){
    return this.itemService.startItem(this.item.id).subscribe();
  }

  pauseItem(){
    return this.itemService.pauseItem(this.item.id).subscribe();
  }


  itemPercent(): string {
    return `${this.rangeInfoService.percent()}%`;
  }

  rangePercent(index: number): string {
    return `${this.rangeInfoService.rangePercent(index)}%`;
  }

  startPercent(index: number): string {
    return `${this.rangeInfoService.rangeStartPercent(index)}%`;
  }

  endPercent(index: number): string {
    return `${this.rangeInfoService.rangeEndPercent(index)}%`;
  }

}
