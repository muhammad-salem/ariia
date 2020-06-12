import { Component, OnInit, Input } from '@angular/core';
import { LogMessage } from '../../../model/log-message';
import { DataService } from '../../../service/data.service';
import { LogFilter } from '../../../model/log-filter';
import { LogService } from '../../../service/log.service';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'ariia-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  filterLog: LogFilter;
  messages: LogMessage[];

  logLevels: string[] = [];
  
  faTrash = faTrash;

  constructor(private dataService: DataService, private logService: LogService,
      private toastr: ToastrService) {
    this.messages = [];
    this.filterLog = {
      level: 'all',
      search: ''
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
        this.toastr.success(`set server log level to ${this.filterLog.level}`, 'successful');
      } else {
        this.toastr.error('server internal error', 'set log level');
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
}
