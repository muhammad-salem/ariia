import { Pipe, PipeTransform } from '@angular/core';
import { LogFilter } from '../model/log-filter';
import { LogMessage } from '../model/log-message';

@Pipe({
  name: 'logFilter'
})
export class LogFilterPipe implements PipeTransform {

  transform(messages: LogMessage[], filter: LogFilter): LogMessage[] {
    return messages.filter(message => message.level <= filter.level);;
  }

}
