import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {LogTableComponent} from './log-table/log-table.component';
import {RouterModule, Routes} from '@angular/router';
import {MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import {MatSortModule} from '@angular/material/sort';
import {MatDividerModule} from '@angular/material/divider';
import {FlexLayoutModule} from '@angular/flex-layout';

const routes: Routes = [
	{ path: '', component: LogTableComponent }
];

@NgModule({
	id: 'logs',
	declarations: [
		LogTableComponent
	],
	imports: [
		CommonModule,
		FormsModule,
		RouterModule.forChild(routes),
		MatFormFieldModule,
		MatInputModule,
		MatTableModule,
		MatToolbarModule,
		MatIconModule,
		MatButtonModule,
		MatSelectModule,
		MatSortModule,
		MatDividerModule,
		FlexLayoutModule
	],
	exports: [
		RouterModule
	],
	bootstrap: []
})
export class LogsModule { }
