import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { ItemFilterPipe } from './pipe/item-filter.pipe';
import { ItemListComponent } from './component/download/item-list/item-list.component';
import { ItemViewComponent } from './component/download/item-view/item-view.component';
import { LogListComponent } from './component/log/log-list/log-list.component';
import { SessionMonitorComponent } from './component/network/session-monitor/session-monitor.component';
import { NetworkChartComponent } from './component/network/network-chart/network-chart.component';
import { HeaderComponent } from './component/site/header/header.component';
import { HomeComponent } from './component/site/home/home.component';
import { NavComponent } from './component/site/nav/nav.component';
import { FooterComponent } from './component/site/footer/footer.component';
import { RootComponent } from './component/site/root/root.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { UpTimePipe } from './pipe/up-time.pipe';
import { LengthUnitPipe } from './pipe/length-unit.pipe';
import { AddLinkComponent } from './component/link/add-link/add-link.component';
import { CircularProgressComponent } from './component/utils/circular-progress/circular-progress.component';
import { ServerPropertiesComponent } from './component/setting/server-properties/server-properties.component';
import { SiteSettingsComponent } from './component/setting/site-settings/site-settings.component';
import { LogFilterPipe } from './pipe/log-filter.pipe';

@NgModule({
  declarations: [
    ItemFilterPipe,
    ItemListComponent,
    ItemViewComponent,
    LogListComponent,
    SessionMonitorComponent,
    NetworkChartComponent,
    HeaderComponent,
    HomeComponent,
    NavComponent,
    FooterComponent,
    RootComponent,
    UpTimePipe,
    LengthUnitPipe,
    AddLinkComponent,
    CircularProgressComponent,
    ServerPropertiesComponent,
    SiteSettingsComponent,
    LogFilterPipe
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    FontAwesomeModule
  ],
  providers: [],
  bootstrap: [RootComponent]
})
export class AppModule { }
