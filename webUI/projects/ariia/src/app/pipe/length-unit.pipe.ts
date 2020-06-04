import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'lengthUnit'
})
export class LengthUnitPipe implements PipeTransform {

	kilobyte: number = 1000;	// use (10^3)
	kibibyte: number = 1024;	// use (2^10)

  transform(length: number, isBinary: boolean): string {
    var k, m, g, t, kilo;
    if(isBinary) {
    	kilo = this.kibibyte;
    } else {
    	kilo = this.kilobyte;
    	isBinary = false;
    }
	k = length / kilo;
	if(k < 1){
		return length + (isBinary ? ' B' : ' b');
	}
	m = k / kilo;
	if(m < 1){
		if (isBinary){
			return `${k.toFixed(3)} KB`;
		} else {
			return `${(k*8).toFixed(3)} Kb`;
		}
	}
	g = m / kilo;
	if(g < 1){
		if (isBinary){
			return `${m.toFixed(3)} MB`;
		} else {
			return `${(m*8).toFixed(3)} Mb`;
		}
	}
	t = g / kilo;
	if(t < 1){
		if (isBinary){
			return `${g.toFixed(3)} GB`;
		} else {
			return `${(g*8).toFixed(3)} Gb`;
		}
	} else {	
		if (isBinary){
			return `${t.toFixed(3)} TB`;
		} else {
			return `${(t*8).toFixed(3)} Tb`;
		}
	}
  }

}
