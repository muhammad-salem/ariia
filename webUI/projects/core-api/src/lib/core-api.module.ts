import {NgModule} from '@angular/core';
import {UpTimePipe} from './pipe/up-time.pipe';
import {SpeedPipe, UnitLengthPipe} from './pipe/speed.pipe';
import {HttpClientModule} from "@angular/common/http";


@NgModule({
	id: 'core-api',
	declarations: [
		UnitLengthPipe,
		SpeedPipe,
		UpTimePipe
	],
	imports: [
		HttpClientModule
	],
	exports: [
		HttpClientModule,
		UnitLengthPipe,
		SpeedPipe,
		UpTimePipe
	]
})
export class CoreApiModule {
}
