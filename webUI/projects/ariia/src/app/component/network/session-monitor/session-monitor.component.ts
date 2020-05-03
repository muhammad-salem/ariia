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

  constructor(private dataService: DataService) { 
    this.session = new NetworkSession();
  }

  ngOnInit(): void {
    this.session = this.dataService.networkSession;
  }

}
