import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {SidebarComponent} from './sidebar/sidebar.component';
import {ServerControlStateComponent} from './server-control-state/server-control-state.component';
import {AppRootComponent} from "./app-root/app-root.component";
import {ThemePickerModule} from '../theme-picker/theme-picker.module';
import {MatDividerModule} from '@angular/material/divider';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {RouterModule} from '@angular/router';
import {MatListModule} from '@angular/material/list';
import {MatButtonModule} from '@angular/material/button';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {FlexLayoutModule} from "@angular/flex-layout";


@NgModule({
	id: 'layout',
	declarations: [
		HeaderComponent,
		FooterComponent,
		SidebarComponent,
		ServerControlStateComponent,
		AppRootComponent

	],
	imports: [
		CommonModule,
		RouterModule,
		ThemePickerModule,
		MatDividerModule,
		MatToolbarModule,
		MatIconModule,
		MatMenuModule,
		MatListModule,
		MatButtonModule,
		MatSidenavModule,
		MatSnackBarModule,
		FlexLayoutModule
	],
	exports: [
		HeaderComponent,
		FooterComponent,
		SidebarComponent,
		ServerControlStateComponent,
		AppRootComponent
	],
	bootstrap: [
		AppRootComponent
	],
	schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class LayoutModule {
}
