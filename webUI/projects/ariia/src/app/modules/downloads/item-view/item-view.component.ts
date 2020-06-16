import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Item, RangeInfoService, ItemService } from 'core-api';

@Component({
  selector: 'app-item-view',
  templateUrl: './item-view.component.html',
  styleUrls: ['./item-view.component.scss']
})
export class ItemViewComponent implements OnInit {

  @Input() item: Item;
  @Output() delete: EventEmitter<void> = new EventEmitter<void>();

  constructor(private itemService: ItemService, private rangeInfoService: RangeInfoService) { }

  ngOnInit(): void {
    this.rangeInfoService.initRangeInfo(this.item.rangeInfo);
  }

  deleteItem() {
    return this.itemService.deleteItem(this.item.id).subscribe(deleted => {
      if (deleted) {
        this.delete.emit();
      }
    });
  }

  startItem() {
    return this.itemService.startItem(this.item.id).subscribe(start => {
      if (start) {
        console.info(`start download item ${this.item}`);
      }
    });
  }

  pauseItem() {
    return this.itemService.pauseItem(this.item.id).subscribe(pause => {
      if (pause) {
        console.info(`pause download item ${this.item}`);
      }
    });
  }

  private percent(): string {
    return this.rangeInfoService.percent().toFixed(2);
  }

  itemPercent(): string {
    return `${this.percent()}%`;
  }

  itemProgress(): number {
    return +this.percent();
  }





  svgFill(index: number): string {
    return this.rangeInfoService.isFinish(index) ? 'green' : 'blue';
  }

  getX(index: number): number {
    return (index % 50) * 15;
  }

  getY(index: number): number {
    return ~~(index / 50) * 15;
  }

  getSvgHeight(): number {
    const rowNum = ~~(this.item?.rangeInfo?.range?.length / 50) + (this.item?.rangeInfo?.range?.length % 50 > 0 ? 1 : 0);
    return rowNum * 15;
  }

}
