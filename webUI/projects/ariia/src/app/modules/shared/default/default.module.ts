import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DefaultComponent } from './default/default.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: '', component: DefaultComponent },
  {
    path: 'dashboard', pathMatch: 'full',
    loadChildren: () => import('../dashboard/dashboard.module')
      .then(module => module.DashboardModule)
  },
  {
    path: 'download', pathMatch: 'full',
    loadChildren: () => import('../../downloads/downloads.module')
      .then(module => module.DownloadsModule)
  },
  {
    path: 'network', pathMatch: 'full',
    loadChildren: () => import('../../network/network.module')
      .then(module => module.NetworkModule)
  },
  {
    path: 'setting', pathMatch: 'full',
    loadChildren: () => import('../../setting/setting.module')
      .then(module => module.SettingModule)
  },
  {
    path: 'logview', pathMatch: 'full',
    loadChildren: () => import('../../logs/logs.module')
      .then(module => module.LogsModule)
  },
  { path: 'redirect/download', redirectTo: 'download', pathMatch: 'full' },
  { path: 'redirect/network', redirectTo: 'network', pathMatch: 'full' },
  { path: 'redirect/setting', redirectTo: 'setting', pathMatch: 'full' },
  { path: 'redirect/logview', redirectTo: 'logview', pathMatch: 'full' },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ],
  exports: [DefaultComponent, RouterModule],
  declarations: [DefaultComponent]
})
export class DefaultModule { }
