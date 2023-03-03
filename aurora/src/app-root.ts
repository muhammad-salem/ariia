import { Component, OnInit, TypeOf } from '@ibyar/aurora';
import { ComponentOutlet } from './route/component-outlet.js';
import { dataService } from './service/services.js';

export interface App {
	title: string;
	component?: TypeOf<object>;
	load: () => Promise<any>;
}

@Component({
	selector: 'app-root',
	template: `
	<h3>{{appName}}</h3>
	<div class="container-fluid w-100 h-100">
		<nav class="nav nav-pills nav-fill gap-1">
			<template *forOf="let app of appList">
				<li class="nav-item">
					<a  class="nav-link" href="javascript:void(0)" 
						[class]="{active: selectedComponent == app.component}"
						@click="lazyLoad(app)"
					>{{app.title}}</a>
				</li>
			</template>
		</nav>
		<div class="w-100 h-100 d-flex flex-direction-column my-2">
			<div class="w-100 h-100">
				<component-outlet [component]="selectedComponent"></component-outlet>
			</div>
		</div>
	</div>`,
	imports: [
		ComponentOutlet,
	]
})
export class AppRoot implements OnInit {

	private dataService = dataService;

	selectedComponent: TypeOf<object> | null = null;
	selectedApp: App;

	appName: string = '';

	lazyLoad(app: App) {
		this.selectedApp = app;
		this.appName = app.title;
		app.load().then(component => this.selectedComponent = app.component = component);
	}

	appList: App[] = [
		{
			title: 'HTTP Fetch',
			load: () => import('./fetch/fetch-app.js').then(module => module.FetchApp),
		},
	];

	onInit(): void {
		// this.dataService.initNotify(this.notifyService);
		this.dataService.initItems();
		this.dataService.initDataService();
		window.onbeforeunload = () => {
			this.dataService.destroy();
		};

		this.appName = 'Ariia';
		setTimeout(() => {
			this.lazyLoad(this.appList.at(-1)!);
		}, 0);
	}

}
