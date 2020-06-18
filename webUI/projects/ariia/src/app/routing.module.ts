import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: 'dashboard', pathMatch: 'full',
    loadChildren: () => import('./modules/dashboard/dashboard.module')
      .then(module => module.DashboardModule)
  },
  {
    path: 'download', pathMatch: 'full',
    loadChildren: () => import('./modules/downloads/downloads.module')
      .then(module => module.DownloadsModule)
  },
  {
    path: 'network', pathMatch: 'full',
    loadChildren: () => import('./modules/network/network.module')
      .then(module => module.NetworkModule)
  },
  {
    path: 'setting', pathMatch: 'full',
    loadChildren: () => import('./modules/setting/setting.module')
      .then(module => module.SettingModule)
  },
  {
    path: 'logview', pathMatch: 'full',
    loadChildren: () => import('./modules/logs/logs.module')
      .then(module => module.LogsModule)
  },
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class RoutingModule { }
