import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {CoreApiModule} from 'core-api';
import {FlexLayoutModule} from '@angular/flex-layout';

import {RoutingModule} from './routing.module';
import {MatSidenavModule} from '@angular/material/sidenav';

import {AppRootComponent} from './app-root/app-root.component';
import {LayoutModule} from './modules/layout/layout.module';
import {MAT_SNACK_BAR_DEFAULT_OPTIONS, MatSnackBarModule} from '@angular/material/snack-bar';


@NgModule({
	id: 'AppModule',
	declarations: [
		AppRootComponent
	],
	providers: [
		{	provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, 
			useValue: {
				duration: 2500,
				horizontalPosition: 'right',
				verticalPosition: 'top'
			}
		}
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		FormsModule,
		HttpClientModule,
		CoreApiModule,
		RoutingModule,
		FlexLayoutModule,
		LayoutModule,
		MatSidenavModule,
		MatSnackBarModule
	],
	exports: [
		AppRootComponent
	],
	bootstrap: [
		AppRootComponent
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {
}
