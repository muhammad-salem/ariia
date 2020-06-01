import { Component, OnInit } from '@angular/core';

import { NetworkSession } from '../../../model/network-session';
import { DataService } from '../../../service/data.service';

@Component({
  selector: 'ariia-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  
  session: NetworkSession;

  constructor(private dataService: DataService) { 
    this.session = new NetworkSession();
  }

  ngOnInit(): void {
    this.session = this.dataService.networkSession;
  }
  
  sessionPercent(): string {
  	if(this.session.totalLength){
  		return `${((this.session.downloadLength/this.session.totalLength) *100 ).toFixed(2)}%`;
  	}
  	return `100%`;
  }

}
