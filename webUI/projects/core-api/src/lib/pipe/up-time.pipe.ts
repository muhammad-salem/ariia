import { Pipe, PipeTransform } from '@angular/core';
import { NetworkSession } from '../model/network-session';

@Pipe({
  name: 'uptime'
})
export class UpTimePipe implements PipeTransform {

  transform(seconds: NetworkSession | number): string {
    if (!seconds) {
      return '00:00:00';
    }
    var timer, hh, mm, ss;
    timer = seconds instanceof NetworkSession ? seconds?.timer : seconds;
    mm = ~~(timer / 60);
    hh = ~~(mm / 60);

    hh = hh % 60;
    mm = mm % 60;
    ss = timer % 60;
    return `${hh ? hh : 0}:${mm ? mm : 0}:${ss}`;
  }

}