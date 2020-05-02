import { Component, OnInit } from '@angular/core';
import { BackboneService } from '../../../service/backbone.service';

@Component({
  selector: 'ariia-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit {
	
	

  constructor(private _backboneService: BackboneService) { }


  ngOnInit(): void {
	this._backboneService.initBackbbone();
  }

}
