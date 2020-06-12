import { Component, OnInit } from '@angular/core';
import { NetworkSession } from '../../../model/network-session';
import { DataService } from '../../../service/data.service';

@Component({
  selector: 'ariia-session-monitor',
  templateUrl: './session-monitor.component.html',
  styleUrls: ['./session-monitor.component.scss']
})
export class SessionMonitorComponent implements OnInit {

  session: NetworkSession;
  isBinary: boolean = true;

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

  sessionProgress(): number {
  	if(this.session.totalLength){
  		return +(((this.session.downloadLength/this.session.totalLength) *100 ).toFixed(2));
  	}
  	return 100;
  }

}
