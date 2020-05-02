import { Component, OnInit, Input } from '@angular/core';
import { NetworkSession } from '../../../model/network-session';
import { BackboneService } from '../../../service/backbone.service';

@Component({
  selector: 'ariia-network-monitor',
  templateUrl: './network-monitor.component.html',
  styleUrls: ['./network-monitor.component.scss']
})
export class NetworkMonitorComponent implements OnInit {

  session: NetworkSession; 
  constructor(private backboneService: BackboneService) { 
    this.session = new NetworkSession();
  }

  ngOnInit(): void {
    this.session = this.backboneService.networkSession;
  }

}
