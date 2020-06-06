
export interface LogMessage {
    timeMillis: number,
    level: string,
    classname: string,
    title: string,
    message: string,
    showMsg: boolean
    clicked: boolean;
}
