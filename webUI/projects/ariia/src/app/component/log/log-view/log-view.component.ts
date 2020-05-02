import { Component, OnInit, Input } from '@angular/core';
import { Message } from '../../../model/message';
import { BackboneService } from '../../../service/backbone.service';

@Component({
  selector: 'ariia-log-view',
  templateUrl: './log-view.component.html',
  styleUrls: ['./log-view.component.scss']
})
export class LogViewComponent implements OnInit {

  logLevel: string[] = ['off', 'log', 'error', 'warn', 'info',
                        'assertion', 'debug', 'trace', 'all'];
  messages: Message[];
  filter: {
    level: string,
    classname: string,
    from: number,
    to: number
  };
  constructor(private backboneService: BackboneService) {
    this.messages = [];
    this.filter = {level: this.logLevel[4], classname: '*', from: 0, to: 0};
  }

  ngOnInit(): void {
    this.messages = this.backboneService.loggingMessage;
  }

  get filterMessages(){
    const filterLevelIndex = this.logLevel.indexOf(this.filter.level);
    return this.messages.filter(message => this.logLevel.indexOf(message.level) <= filterLevelIndex);
  }

}
