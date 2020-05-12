import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './component/site/home/home.component';
import { ItemListComponent } from './component/download/item-list/item-list.component';
import { LogListComponent } from './component/log/log-list/log-list.component';
import { NetworkChartComponent } from './component/network/network-chart/network-chart.component';


const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'download', component: ItemListComponent},
  { path: 'logging', component: LogListComponent},
  { path: 'network', component: NetworkChartComponent},
  { path: '', pathMatch: 'full', redirectTo: 'home' },
  { path: 'redirect/home',   redirectTo: 'home', pathMatch: 'full' },
  { path: 'redirect/download',   redirectTo: 'download', pathMatch: 'full' },
  { path: 'redirect/logging',   redirectTo: 'logging', pathMatch: 'full' },
  { path: 'redirect/network',   redirectTo: 'network', pathMatch: 'full' },
  { path: '**', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
