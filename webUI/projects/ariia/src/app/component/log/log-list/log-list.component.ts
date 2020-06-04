import { Component, OnInit, Input } from '@angular/core';
import { LogMessage } from '../../../model/log-message';
import { DataService } from '../../../service/data.service';
import { LogFilter } from '../../../model/log-filter';
import { LogService } from '../../../service/log.service';

@Component({
  selector: 'ariia-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  filter: LogFilter;
  messages: LogMessage[];

  logLevels: string[] = [];

  constructor(private dataService: DataService, private logService: LogService) {
    this.messages = [];
    this.filter = {
      level: 'all',
      classname: '*'
    }
   }

  ngOnInit(): void {
    this.messages = this.dataService.loggingMessage;
    this.logService.levelValues().subscribe(values => this.logLevels = values);
    this.logService.getLevel().subscribe(level => this.filter.level = level);
  }

  setLevel(level: string) {
    this.logService.setLevel(level).subscribe(isDone => {
      if (isDone){
        this.filter.level = level;
      }
    });
  }

}
