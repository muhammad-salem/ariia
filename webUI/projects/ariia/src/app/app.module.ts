import { BrowserModule } from '@angular/platform-browser';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CoreApiModule } from 'core-api';
import { FlexLayoutModule } from '@angular/flex-layout';

import { RoutingModule } from './routing.module';
import { MatSidenavModule } from '@angular/material/sidenav';

import { AppRootComponent } from './app-root/app-root.component';
import { LayoutModule } from './modules/layout/layout.module';


@NgModule({
  id: 'AppModule',
  declarations: [
    AppRootComponent
  ],
  providers: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    CoreApiModule,
    RoutingModule,
    FlexLayoutModule,
    LayoutModule,
    MatSidenavModule
  ],
  exports: [
    AppRootComponent
  ],
  bootstrap: [AppRootComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }
