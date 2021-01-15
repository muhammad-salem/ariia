import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { SpeedPipe, UnitLengthPipe } from './pipe/speed.pipe';
import { UpTimePipe } from './pipe/up-time.pipe';



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
		UnitLengthPipe,
		SpeedPipe,
		UpTimePipe
	]
})
export class CoreApiModule { }
