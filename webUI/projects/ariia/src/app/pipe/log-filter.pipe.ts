import { Pipe, PipeTransform } from '@angular/core';
import { LogMessage } from '../model/log-message';
import { LogFilter } from '../model/log-filter';

@Pipe({
  name: 'logFilter'
})
export class LogFilterPipe implements PipeTransform {

  transform(messages: LogMessage[], filter: LogFilter): LogMessage[] {
    if(filter.classname === '*' || filter.classname === '') {
    	messages.forEach(msg => msg.showMsg = true);
  	} else {
	  	messages.forEach(msg => msg.showMsg = msg.classname.includes(filter.classname));
  	}
  	console.log(messages, filter);
  	return messages;
  }

}
