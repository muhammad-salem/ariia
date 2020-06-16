import { BrowserModule } from '@angular/platform-browser';
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CoreApiModule } from 'core-api';
import { FlexLayoutModule } from '@angular/flex-layout';

import { RoutingModule } from './routing.module';
import { MaterialModule } from './modules/material/material.module';
import { ThemePickerModule } from './modules/shared/theme-picker/theme-picker.module';

import { AppRootComponent } from './app-root/app-root.component';
import { LayoutModule } from './modules/shared/layout/layout.module';

@NgModule({
  declarations: [AppRootComponent],
  providers: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    CoreApiModule,
    RoutingModule,
    FlexLayoutModule,
    MaterialModule,
    LayoutModule,
    ThemePickerModule
  ],
  exports: [AppRootComponent],
  bootstrap: [AppRootComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule { }
