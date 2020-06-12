import { Pipe, PipeTransform } from '@angular/core';
import { LogMessage } from '../model/log-message';

@Pipe({
  name: 'searchLog'
})
export class SearchLogPipe implements PipeTransform {

  transform(messages: LogMessage[], search: string): LogMessage[] {
    if(search === '*' || search === '') {
    	messages.forEach(msg => msg.showMsg = true);
  	} else {
	  	messages.forEach(
			  msg => msg.showMsg = 
			  JSON.stringify(msg).toLowerCase().includes(search.toLowerCase()));
  	}
  	return messages;
  }

}
