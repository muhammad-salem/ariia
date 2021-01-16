import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreApiModule } from 'core-api';
import { RoutingModule } from './routing.module';
import { LayoutModule } from './modules/layout/layout.module';
import { AppRootComponent } from './modules/layout/app-root/app-root.component';


@NgModule({
	id: 'AppModule',
	declarations: [],
	providers: [
		{
			provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
			useValue: {
				duration: 2500,
				horizontalPosition: 'center',
				verticalPosition: 'top'
			}
		}
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		FormsModule,
		CoreApiModule,
		RoutingModule,
		LayoutModule
	],
	bootstrap: [
		AppRootComponent
	]
})
export class AppModule { }
