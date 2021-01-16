import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteSettingsComponent} from './site-settings/site-settings.component';
import {ServerSettingsComponent} from './server-settings/server-settings.component';
import {SiteSettingsViewerComponent} from './site-settings-viewer/site-settings-viewer.component';

import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
	{ path: '', component: SiteSettingsViewerComponent }
];

@NgModule({
	id: 'settings',
	declarations: [
		SiteSettingsComponent,
		ServerSettingsComponent,
		SiteSettingsViewerComponent
	],
	imports: [
		CommonModule,
		RouterModule.forChild(routes)
	]
})
export class SettingModule { }
