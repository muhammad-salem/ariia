import { Pipe, PipeTransform } from '@ibyar/aurora';
import { NetworkConfig } from '../model/network-session';
import { DataService } from '../service/data.service';


const kiloByte = 1000;	// use (10^3)
const kibiByte = 1024;	// use (2^10)

function toUnitLength(length: number, networkConfig: NetworkConfig): string {
	if (Number.isNaN(length) || !length) {
		return '0 b';
	}
	let k: number, m: number, g: number, t: number, kilo: number;
	if (networkConfig.isBinary) {
		kilo = kibiByte;
	} else {
		kilo = kiloByte;
	}
	k = length / kilo;
	if (k < 1) {
		return length + (networkConfig.isByte ? ' B' : ' b');
	}
	m = k / kilo;
	if (m < 1) {
		if (networkConfig.isByte) {
			return `${k.toFixed(3)} KB`;
		} else {
			return `${(k * 8).toFixed(3)} Kb`;
		}
	}
	g = m / kilo;
	if (g < 1) {
		if (networkConfig.isByte) {
			return `${m.toFixed(3)} MB`;
		} else {
			return `${(m * 8).toFixed(3)} Mb`;
		}
	}
	t = g / kilo;
	if (t < 1) {
		if (networkConfig.isByte) {
			return `${g.toFixed(3)} GB`;
		} else {
			return `${(g * 8).toFixed(3)} Gb`;
		}
	} else {
		if (networkConfig.isByte) {
			return `${t.toFixed(3)} TB`;
		} else {
			return `${(t * 8).toFixed(3)} Tb`;
		}
	}
}

@Pipe({
	name: 'unitLength'
})
export class UnitLengthPipe implements PipeTransform<number, string> {

	networkConfig: NetworkConfig;

	constructor(dataService: DataService) {
		this.networkConfig = dataService.networkConfig;
	}

	transform(length: number): string {
		return toUnitLength(length, this.networkConfig);
	}
}


@Pipe({
	name: 'speed'
})
export class SpeedPipe implements PipeTransform<number, string> {

	networkConfig: NetworkConfig;

	constructor(dataService: DataService) {
		this.networkConfig = dataService.networkConfig;
	}

	transform(length: number): string {
		return toUnitLength(length, this.networkConfig) + '/s';
	}

}
