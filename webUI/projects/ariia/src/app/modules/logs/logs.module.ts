import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {LogTableComponent} from './log-table/log-table.component';
import {RouterModule, Routes} from '@angular/router';
import {MatTableModule} from '@angular/material/table';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatToolbarModule} from '@angular/material/toolbar';
import {FlexModule} from '@angular/flex-layout';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatSelectModule} from '@angular/material/select';
import {MatSortModule} from '@angular/material/sort';
import {MatDividerModule} from '@angular/material/divider';

const routes: Routes = [
	{path: '', component: LogTableComponent},
	{path: '**', redirectTo: ''}
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
		FlexModule,
		MatIconModule,
		MatButtonModule,
		MatSelectModule,
		MatSortModule,
		MatDividerModule
	],
	bootstrap: []
})
export class LogsModule {
}
