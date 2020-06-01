import { Component, OnInit, Input, ElementRef } from '@angular/core';

@Component({
  selector: 'ariia-circular-progress',
  templateUrl: './circular-progress.component.html',
  styleUrls: ['./circular-progress.component.scss']
})
export class CircularProgressComponent implements OnInit {

	@Input() dataValue: number = 0;
	@Input() borderClass: string = 'border-primary';
	
  constructor() { }

  ngOnInit(): void { }
  
  rotateRight(): string {
  	if (this.dataValue <= 50 && this.dataValue > 0) {
	    return `rotate(${this.percentageToDegrees(this.dataValue)}deg)`;
	  } else if (this.dataValue > 50) {
	    return `rotate(180deg)`;
	  } else {
	    return null;
	  }
  }
  
  rotateLeft(): string {
  	if (this.dataValue > 50) {
  		return `rotate(${this.percentageToDegrees(this.dataValue - 50)}deg)`;
  	} else {
  		return null;
  	}
  }
  
  
  
  percentageToDegrees(percentage: number): number {
    return percentage / 100 * 360;
  }
  
/**    
  init(){
  	 $(".progress").each(function() {

		var value = $(this).attr('data-value');
		var left = $(this).find('.progress-left .progress-bar');
		var right = $(this).find('.progress-right .progress-bar');

		if (value > 0) {
		  if (value <= 50) {
		    right.css('transform', 'rotate(' + percentageToDegrees(value) + 'deg)')
		  } else {
		    right.css('transform', 'rotate(180deg)')
		    left.css('transform', 'rotate(' + percentageToDegrees(value - 50) + 'deg)')
		  }
		}

	  });
  }
**/

}
