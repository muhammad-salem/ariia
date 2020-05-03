import { Component, OnInit } from '@angular/core';
import { faHome, faTerminal, faWifi,faCloudDownloadAlt } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'ariia-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss']
})
export class NavComponent implements OnInit {

  faCloudDownloadAlt = faCloudDownloadAlt;
  faHome = faHome;
  faTerminal = faTerminal;
  faWifi = faWifi;
  
  constructor() { }

  ngOnInit(): void {
  }

}
