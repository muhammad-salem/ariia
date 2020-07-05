import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {FormsModule} from '@angular/forms';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {FlexLayoutModule} from "@angular/flex-layout";

import {DashboardComponent} from './dashboard/dashboard.component';
import {LinkComponent} from './link/link.component';
import {MetaLinkComponent} from './meta-link/meta-link.component';
import {AddLinkViewerComponent} from './add-link-viewer/add-link-viewer.component';


const routes: Routes = [
	{path: '', component: DashboardComponent},
	{path: '**', redirectTo: ''}
];

@NgModule({
	id: 'dashboard',
	declarations: [
		DashboardComponent,
		LinkComponent,
		MetaLinkComponent,
		AddLinkViewerComponent
	],
	imports: [
		CommonModule,
		RouterModule.forChild(routes),
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatIconModule,
		MatDividerModule,
		MatSnackBarModule,
		MatButtonModule,
		MatListModule,
		FlexLayoutModule
	],
	exports: [
		RouterModule
	],
	bootstrap: [DashboardComponent]
})
export class DashboardModule {
}
