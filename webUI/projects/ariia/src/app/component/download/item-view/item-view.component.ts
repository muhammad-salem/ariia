import { Component, OnInit, Input } from '@angular/core';
import { Item } from '../../../model/item';
import { RangeInfoService } from '../../../service/range-info.service';

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

  constructor(private rangeInfoService: RangeInfoService) { }

  ngOnInit(): void {
    this.rangeInfoService.initRangeInfo(this.item.rangeInfo);
  }

  itemPercent(): string {
    return `${this.rangeInfoService.percent()}%`;
    // return `50%`;
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
