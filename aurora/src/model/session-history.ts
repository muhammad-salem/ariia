import { SessionReport } from './network-session';

export interface SessionHistory {
	x: Date;
	y: number;
	session?: SessionReport;
}
