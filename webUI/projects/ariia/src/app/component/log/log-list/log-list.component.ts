import { Component, OnInit, Input } from '@angular/core';
import { LogMessage } from '../../../model/log-message';
import { LogLevel } from '../../../model/log-level.enum';
import { DataService } from '../../../service/data.service';
import { LogFilter } from '../../../model/log-filter';

@Component({
  selector: 'ariia-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  filter: LogFilter;
  messages: LogMessage[];

  constructor(private dataService: DataService) {
    this.messages = [];
    this.filter = {
      level: LogLevel.info,
      classname: '*',
      from: 0,
      to: 0
    }
   }

  ngOnInit(): void {
    this.messages = this.dataService.loggingMessage;
  }

}
