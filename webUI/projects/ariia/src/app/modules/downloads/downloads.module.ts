import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {MatTableModule} from '@angular/material/table';
import {CoreApiModule} from 'core-api';

import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatTabsModule} from '@angular/material/tabs';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {FlexLayoutModule} from '@angular/flex-layout';
import {DownloadTabsComponent} from './download-tabs/download-tabs.component';
import {DownloadViewComponent} from './download-view/download-view.component';
import {DownloadListComponent} from './download-list/download-list.component';
import {DownloadTableComponent} from './download-table/download-table.component';
import {MatDividerModule} from '@angular/material/divider';


const routes: Routes = [
	{path: '', component: DownloadTabsComponent}
];

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
		RouterModule.forChild(routes),
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
		FlexLayoutModule,
		MatDividerModule
	],
	exports: [
		CommonModule,
		RouterModule,
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
		FlexLayoutModule
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
