import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'uptime'
})
export class UpTimePipe implements PipeTransform {

  transform(seconds: number): string {
    var hh, mm, ss;
    mm = ~~(seconds / 60);
    hh = ~~(mm / 60);

    hh = hh % 60;
    mm = mm % 60;
    ss = seconds % 60;
	return `${hh?hh:0}:${mm?mm:0}:${ss}`;
  }

}
