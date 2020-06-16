import { Component, OnInit } from '@angular/core';
import { DataService } from 'core-api';

@Component({
  selector: 'app-root',
  templateUrl: './app-root.component.html',
  styleUrls: ['./app-root.component.scss']
})
export class AppRootComponent implements OnInit {

  sideBarOpen: boolean = true;

  constructor(private dataService: DataService) { }

  ngOnInit(): void {
    this.dataService.initItems();
    this.dataService.initDataService();

    window.onbeforeunload = () => {
      this.dataService.destroy();
    };

  }

}
