import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {DashboardComponent} from './dashboard/dashboard.component';
import {LinksModule} from '../links/links.module';

const routes: Routes = [
	{path: '', component: DashboardComponent},
	{path: '**', redirectTo: ''}
];

@NgModule({
	id: 'dashboard',
	declarations: [DashboardComponent],
	imports: [
		CommonModule,
		RouterModule.forChild(routes),
		LinksModule
	],
	exports: [DashboardComponent],
	bootstrap: [DashboardComponent]
})
export class DashboardModule {
}
