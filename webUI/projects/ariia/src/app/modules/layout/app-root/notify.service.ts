import {Injectable} from '@angular/core';
import {NotificationConfig} from 'core-api';
import {MatSnackBar} from '@angular/material/snack-bar';


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
