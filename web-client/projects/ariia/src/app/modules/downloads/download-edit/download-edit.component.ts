import { Component, Inject } from '@angular/core';
import { Item } from 'core-api';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
	selector: 'download-edit',
	templateUrl: './download-edit.component.html',
	styleUrls: ['./download-edit.component.scss']
})
export class DownloadEditComponent {

	constructor(public dialogRef: MatDialogRef<DownloadEditComponent>,@Inject(MAT_DIALOG_DATA) public item: Item) {}

	onNoClick(): void {
		this.dialogRef.close();
	}

}
