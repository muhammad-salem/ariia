import { LogLevel } from './log-level.enum';

export class LogFilter {
    level: LogLevel;
    classname: string;
    from: number;
    to: number;
}
