import {Component, OnInit, ViewChild} from '@angular/core';
import {DataService, LogService, Message} from 'core-api';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';


@Component({
	selector: 'app-log-table',
	templateUrl: './log-table.component.html',
	styleUrls: ['./log-table.component.scss']
})
export class LogTableComponent implements OnInit {

	dataSource: MatTableDataSource<Message>;
	@ViewChild(MatSort, {static: true}) sort: MatSort;

	level = 'info';
	logLevels: string[] = [];
	// logLevels: string[] = ['off', 'log', 'error', 'warn', 'info', 'assertion', 'debug', 'trace']; // [];

	columnsToDisplay = [
		'timeMillis',
		'level',
		'classname',
		'title',
		'message'
	];

	constructor(private dataService: DataService, private logService: LogService) {
	}

	ngOnInit(): void {
		this.dataSource = new MatTableDataSource(this.dataService.loggingMessage);
		this.dataSource.connect = () => this.dataService.logSubject;
		this.dataSource.sort = this.sort;
		this.dataSource.filterPredicate = (message: Message, filter: string) => {
			console.log(filter, message);
			return false;
		};
		this.logService.levelValues().subscribe(values => this.logLevels = values);
		this.logService.getLevel().subscribe(level => this.level = level);
	}

	setLevel() {
		this.logService.setLevel(this.level).subscribe(isDone => {
			if (isDone) {
				// TO:DO
				// show success notification
				// this.toastr.success(`set server log level to ${this.filter.level}`, 'successful');
			} else {
				// show error notification
				// this.toastr.error('server internal error', 'set log level');
			}
		});
	}

	applyFilter(value: string) {
		this.dataSource.filter = value.trim().toLowerCase();
	}

	// selectMessage(message: Message) {
	//   this.messages.forEach(m => m.clicked = false);
	//   message.clicked = true;
	// }

	clearLogMessages() {
		this.dataService.loggingMessage.splice(0, this.dataService.loggingMessage.length);
		this.dataService.logSubject.next(this.dataService.loggingMessage);
	}


}
