import {NgModule} from '@angular/core';
import {UpTimePipe} from './pipe/up-time.pipe';
import {SpeedPipe, UnitLengthPipe} from './pipe/speed.pipe';


@NgModule({
	id: 'core-api',
	declarations: [UnitLengthPipe, SpeedPipe, UpTimePipe],
	imports: [],
	exports: [UnitLengthPipe, SpeedPipe, UpTimePipe]
})
export class CoreApiModule {
}
