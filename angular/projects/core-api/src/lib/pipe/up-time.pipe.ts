import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
	name: 'uptime'
})
export class UpTimePipe implements PipeTransform {


	twoDigit(num: number): string {
		return num ? ((num > 9) ? num.toString() : '0' + num.toString()) : '00';
	}

	transform(seconds: number): string {
		if (!seconds || seconds === 1) {
			return '00:00:00';
		}
		if (seconds < 10) {
			return `00:00:0${seconds}`;
		} else if (seconds < 60) {
			return `00:00:${this.twoDigit(seconds)}`;
		} else if (seconds < 3600) {
			return `00:${this.twoDigit(~~(seconds / 60) % 60)}:${this.twoDigit(seconds % 60)}`;
		} else {
			let hh: number, mm: number, ss: number;
			mm = ~~(seconds / 60);
			hh = ~~(mm / 60);

			hh = hh % 60;
			mm = mm % 60;
			ss = seconds % 60;
			return `${this.twoDigit(hh)}:${this.twoDigit(mm)}:${this.twoDigit(ss)}`;
			// return `${hh ? hh : '00'}:${mm ? mm : '00'}:${ss}`;
		}
	}

}
