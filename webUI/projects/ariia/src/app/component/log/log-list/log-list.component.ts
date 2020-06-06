import { Component, OnInit, OnChanges, Input, SimpleChanges } from '@angular/core';
import { LogMessage } from '../../../model/log-message';
import { DataService } from '../../../service/data.service';
import { LogFilter } from '../../../model/log-filter';
import { LogService } from '../../../service/log.service';
import { faTrash } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'ariia-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit, OnChanges {

  filterLog: LogFilter;
  messages: LogMessage[];

  logLevels: string[] = [];
  
  faTrash = faTrash;

  constructor(private dataService: DataService, private logService: LogService) {
    this.messages = [];
    this.filterLog = {
      level: 'all',
      classname: ''
    }
   }

  ngOnInit(): void {
    this.messages = this.dataService.loggingMessage;
    this.logService.levelValues().subscribe(values => this.logLevels = values);
    this.logService.getLevel().subscribe(level => this.filterLog.level = level);
  }

  setLevel() {
    this.logService.setLevel(this.filterLog.level).subscribe(isDone => {
      if (isDone){
        // toster
      }
    });
  }
  
  selectMessage(message: LogMessage) {
  	this.messages.forEach(m => m.clicked = false);
  	message.clicked = true;
  }
  
  clearLogMessages() {
  	this.messages.splice(0, this.messages.length);
  }
  
  ngOnChanges(changes: SimpleChanges): void {
  	console.log('changes', changes);
  }
}
