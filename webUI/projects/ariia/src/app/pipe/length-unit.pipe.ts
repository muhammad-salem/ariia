import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'lengthUnit'
})
export class LengthUnitPipe implements PipeTransform {

	kBites: number = 1000;	// use (10^3)
	kBytes: number = 1024;	// use (2^10)

  transform(length: number, isByte: boolean): string {
    var k, m, g, t, kilo;
    if(isByte) {
    	kilo = this.kBytes;
    } else {
    	kilo = this.kBites;
    	isByte = false;
    }
	k = length / kilo;
	if(k < 1){
		return length + (isByte ? ' B' : ' b');
	}
	m = k / kilo;
	if(m < 1){
		if (isByte){
			return `${k.toFixed(3)} KB`;
		} else {
			return `${(k*8).toFixed(3)} Kb`;
		}
	}
	g = m / kilo;
	if(g < 1){
		if (isByte){
			return `${m.toFixed(3)} MB`;
		} else {
			return `${(m*8).toFixed(3)} Mb`;
		}
	}
	t = g / kilo;
	if(t < 1){
		if (isByte){
			return `${g.toFixed(3)} GB`;
		} else {
			return `${(g*8).toFixed(3)} Gb`;
		}
	} else {	
		if (isByte){
			return `${t.toFixed(3)} TB`;
		} else {
			return `${(t*8).toFixed(3)} Tb`;
		}
	}
  }

}
