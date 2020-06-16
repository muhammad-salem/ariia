import { Ui } from './ui';

export interface Message extends Ui {
    timeMillis: number;
    level: string;
    classname: string;
    title: string;
    message: string;
}