import {Component, OnInit} from '@angular/core';
import {DataService, LogService, Message} from 'core-api';
import {MatTableDataSource} from '@angular/material/table';


@Component({
	selector: 'app-log-table',
	templateUrl: './log-table.component.html',
	styleUrls: ['./log-table.component.scss']
})
export class LogTableComponent implements OnInit {

	dataSource: MatTableDataSource<Message>;

	level: string = 'info';
	logLevels: string[] = [];

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

	applyFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.dataSource.filter = filterValue.trim().toLowerCase();
		// console.log(filterValue);
	}

	// selectMessage(message: Message) {
	//   this.messages.forEach(m => m.clicked = false);
	//   message.clicked = true;
	// }

	// clearLogMessages() {
	//   this.messages.splice(0, this.messages.length);
	// }


}
