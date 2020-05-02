import { Component, OnInit, Input } from '@angular/core';
import { Message } from '../../../model/message';

@Component({
  selector: 'ariia-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.scss']
})
export class LogListComponent implements OnInit {

  @Input() messages: Message[];
  constructor() { 
    this.messages = [];
  }

  ngOnInit(): void {
  }

}
