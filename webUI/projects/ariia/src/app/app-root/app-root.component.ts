import {Component, Inject, Injectable, OnInit, Optional} from '@angular/core';
import {DataService, NotificationConfig} from 'core-api';
import {MAT_SNACK_BAR_DATA, MatSnackBar} from '@angular/material/snack-bar';

@Component({
	selector: 'app-notify-snack-bar',
	template: `
		<div>
			<div class="display-1">{{data.title}}</div>
			<div class="h3" *ngIf="data.message">{{data.message}}</div>
		</div>
	`,
})
export class NotifySnackBarComponent {
	constructor(@Optional() @Inject(MAT_SNACK_BAR_DATA) public data: { title: string, message: string}) {}
}

@Injectable({
	providedIn: 'root'
})
export class NotifyService extends NotificationConfig {
	constructor(private snackBar: MatSnackBar) {
		super();
	}
	showSnackBar(title: string, message?: string): void {
		const snackBarRef = this.snackBar.open(title);
		/*
		const snackBarRef = this.snackBar.openFromComponent(NotifySnackBarComponent, {
			duration: 4 * 1000,
			horizontalPosition: 'right',
			verticalPosition: 'top',
			data: {title, message}
		});
		snackBarRef.afterDismissed().subscribe(() => {
			console.log('The snack-bar was dismissed');
		});
		snackBarRef.onAction().subscribe(() => {
			console.log('The snack-bar action was triggered!');
		});
		*/
	}

	info(title: string, message?: string): void {
		try {
			this.showSnackBar(title, message);
		} catch (e) {
			console.error(title, message, e);
		}
	}

	success(title: string, message?: string): void {
		try {
			this.showSnackBar(title, message);
		} catch (e) {
			console.error(title, message, e);
		}
	}

	error(title: string, message?: string): void {
		try {
			this.showSnackBar(title, message);
		} catch (e) {
			console.error(title, message, e);
		}
	}
}

@Component({
	selector: 'app-root',
	templateUrl: './app-root.component.html',
	styleUrls: ['./app-root.component.scss']
})
export class AppRootComponent implements OnInit {

	sideBarOpen = true;

	constructor(private dataService: DataService, private notifyService: NotifyService) {
	}

	ngOnInit(): void {
		this.dataService.initNotify(this.notifyService);
		this.dataService.initItems();
		this.dataService.initDataService();
		window.onbeforeunload = () => {
			this.dataService.destroy();
		};
	}

}
