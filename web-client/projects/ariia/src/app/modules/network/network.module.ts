import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkChartComponent } from './network-chart/network-chart.component';
import { SessionMonitorComponent } from './session-monitor/session-monitor.component';
import { NetworkViewerComponent } from './network-viewer/network-viewer.component';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { CoreApiModule } from 'core-api';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';

const routes: Routes = [ { path: '', component: NetworkViewerComponent } ];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class NetworkRoutingModule {

}


@NgModule({
	id: 'network',
	declarations: [
		NetworkChartComponent,
		SessionMonitorComponent,
		NetworkViewerComponent
	],
	imports: [
		CommonModule,
		NetworkRoutingModule,
		CoreApiModule,
		MatCardModule,
		MatListModule,
		MatProgressBarModule
	],
	exports: [
		NetworkChartComponent,
		SessionMonitorComponent,
		NetworkViewerComponent
	],
	bootstrap: [
		NetworkViewerComponent
	]
})
export class NetworkModule {

}
