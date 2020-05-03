import { Component, OnInit, Input } from '@angular/core';
import { LogMessage } from '../../../model/log-message';

@Component({
  selector: 'ariia-log-view',
  templateUrl: './log-view.component.html',
  styleUrls: ['./log-view.component.scss']
})
export class LogViewComponent implements OnInit {

  @Input() message: LogMessage;

  constructor() {}

  ngOnInit(): void {
  }

}
