import { Pipe, PipeTransform } from '@angular/core';
import { Ui } from '../model/ui';
import { Item } from '../model/item';
import { Message } from '../model/message';

@Pipe({
  name: 'search'
})
export class SearchPipe implements PipeTransform {

  transform(array: any, search: string): any;
  transform<Item>(array: Item[], search: string): Item[];
  transform<Message>(array: Message[], search: string): Message[];
  transform<T extends Ui>(array: T[], search: string): T[] {
    if (search === '*' || search === '') {
      array.forEach(item => item.show = true);
    } else {
      array.forEach(
        item => item.show =
          JSON.stringify(item).toLowerCase().includes(search.toLowerCase()));
    }
    return array;
  }

}
