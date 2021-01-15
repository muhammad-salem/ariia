import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NetworkChartComponent } from './network-chart/network-chart.component';
import { SessionMonitorComponent } from './session-monitor/session-monitor.component';
import { NetworkViewerComponent } from './network-viewer/network-viewer.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
	{ path: '', component: NetworkViewerComponent }
];
@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class NetworkRoutingModule { }


@NgModule({
	declarations: [
		NetworkChartComponent,
		SessionMonitorComponent,
		NetworkViewerComponent
	],
	imports: [
		CommonModule,
		NetworkRoutingModule
	],
	exports: [
		NetworkRoutingModule
	],
	bootstrap: [
		NetworkChartComponent,
		SessionMonitorComponent,
		NetworkViewerComponent
	]
})
export class NetworkModule { }
