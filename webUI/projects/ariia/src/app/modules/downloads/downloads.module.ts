import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { MatTableModule } from '@angular/material/table';
import { CoreApiModule } from 'core-api';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { DownloadTabsComponent } from './download-tabs/download-tabs.component';
import { DownloadViewComponent } from './download-view/download-view.component';
import { DownloadListComponent } from './download-list/download-list.component';
import { DownloadTableComponent } from './download-table/download-table.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FlexLayoutModule } from "@angular/flex-layout";

const routes: Routes = [
	{ path: '', component: DownloadTabsComponent }
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class DownloadsRoutingModule { }

@NgModule({
	id: 'downloads',
	declarations: [
		DownloadViewComponent,
		DownloadListComponent,
		DownloadTableComponent,
		DownloadTabsComponent
	],
	imports: [
		CommonModule,
		DownloadsRoutingModule,
		CoreApiModule,
		MatTableModule,
		MatProgressBarModule,
		MatCheckboxModule,
		MatTabsModule,
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
	],
	exports: [
		CommonModule,
		DownloadsRoutingModule,
		CoreApiModule,
		MatTableModule,
		MatProgressBarModule,
		MatCheckboxModule,
		MatTabsModule,
		MatIconModule,
		MatCardModule,
		MatGridListModule,
		MatPaginatorModule,
		MatSortModule,
		MatFormFieldModule,
		MatInputModule,
		MatToolbarModule,
		MatButtonModule
	],
	bootstrap: [
		DownloadViewComponent,
		DownloadListComponent,
		DownloadTableComponent,
		DownloadTabsComponent
	]
})
export class DownloadsModule {
}
