import { LogLevel } from './log-level.enum';

export class LogMessage {
    timeMillis: number;
    level: LogLevel;
    classname: string;
    title: string;
    message: string;
}
