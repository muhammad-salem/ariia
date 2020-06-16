import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-download-viewer',
  templateUrl: './download-viewer.component.html',
  styleUrls: ['./download-viewer.component.scss']
})
export class DownloadViewerComponent implements OnInit {

  navLinks: { path: string, label: string, icon: string }[] = [
    { path: 'table', label: 'Download Table', icon: 'table' },
    { path: 'list', label: 'Download List', icon: 'list' }
  ];
  constructor() { }

  ngOnInit(): void {
  }

}
