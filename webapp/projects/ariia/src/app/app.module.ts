import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './component/website/home-page/home-page.component';
import { AboutPageComponent } from './component/website/about-page/about-page.component';
import { LayoutComponent } from './component/structure/layout/layout.component';
import { HeaderComponent } from './component/structure/header/header.component';
import { FooterComponent } from './component/structure/footer/footer.component';
import { SiteNavbarComponent } from './component/structure/site-navbar/site-navbar.component';
import { LoginComponent } from './component/user/login/login.component';
import { LogoutComponent } from './component/user/logout/logout.component';
import { UserProfileComponent } from './component/user/user-profile/user-profile.component';
import { HeaderUserProfileComponent } from './component/user/header-user-profile/header-user-profile.component';
import { ItemComponent } from './component/downloads/item/item.component';
import { DownloadListComponent } from './component/downloads/download-list/download-list.component';
import { CompactItemViewComponent } from './component/downloads/compact-item-view/compact-item-view.component';
import { HistoryComponent } from './component/downloads/history/history.component';
import { AddLinkComponent } from './component/downloads/add-link/add-link.component';
import { SiteSettingComponent } from './component/server/site-setting/site-setting.component';
import { ServerSettingComponent } from './component/server/server-setting/server-setting.component';
import { NotificationComponent } from './component/notify/notification/notification.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    AboutPageComponent,
    LayoutComponent,
    HeaderComponent,
    FooterComponent,
    SiteNavbarComponent,
    LoginComponent,
    LogoutComponent,
    UserProfileComponent,
    HeaderUserProfileComponent,
    ItemComponent,
    DownloadListComponent,
    CompactItemViewComponent,
    HistoryComponent,
    AddLinkComponent,
    SiteSettingComponent,
    ServerSettingComponent,
    NotificationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
