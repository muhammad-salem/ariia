import { Pipe, PipeTransform } from '@angular/core';
import { Item } from '../model/item';

@Pipe({
  name: 'itemFilter'
})
export class ItemFilterPipe implements PipeTransform {

  transform(items: Item[], filter: any): Item[] {
    return items;
  }

}
