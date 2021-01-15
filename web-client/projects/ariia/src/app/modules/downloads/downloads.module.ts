import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { CoreApiModule } from 'core-api';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DownloadViewComponent } from './download-view/download-view.component';
import { DownloadTableComponent } from './download-table/download-table.component';

const routes: Routes = [
	{ path: '', component: DownloadTableComponent },
	{ path: ':id', component: DownloadViewComponent },
];

@NgModule({
	id: 'download',
	declarations: [
		DownloadViewComponent,
		DownloadTableComponent
	],
	imports: [
		CommonModule,
		RouterModule.forChild(routes),
		CoreApiModule,
		MatTableModule,
		MatProgressBarModule,
		MatCheckboxModule,
		MatIconModule,
		MatCardModule,
		MatGridListModule,
		MatPaginatorModule,
		MatSortModule,
		MatFormFieldModule,
		MatInputModule,
		MatToolbarModule,
		MatButtonModule,
		MatDividerModule,
		MatListModule,
		MatProgressSpinnerModule,
		FlexLayoutModule
	]
})
export class DownloadsModule {
}
