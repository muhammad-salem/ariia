import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: 'dashboard',
		loadChildren: () => import('./modules/dashboard/dashboard.module')
			.then(module => module.DashboardModule)
	},
	{
		path: 'download',
		loadChildren: () => import('./modules/downloads/downloads.module')
			.then(module => module.DownloadsModule)
	},
	{
		path: 'network',
		loadChildren: () => import('./modules/network/network.module')
			.then(module => module.NetworkModule)
	},
	{
		path: 'config',
		loadChildren: () => import('./modules/setting/setting.module')
			.then(module => module.SettingModule)
	},
	{
		path: 'logger',
		loadChildren: () => import('./modules/logs/logs.module')
			.then(module => module.LogsModule)
	},
	{ path: '', redirectTo: '/dashboard', pathMatch: 'full' },
	{ path: '**', redirectTo: '/dashboard' }
];

@NgModule({
	imports: [RouterModule.forRoot(
		routes,
		// { enableTracing: true }
	)],
	exports: [RouterModule]
})
export class RoutingModule {
}
