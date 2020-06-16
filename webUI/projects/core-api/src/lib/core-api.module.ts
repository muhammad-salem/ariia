import { NgModule } from '@angular/core';
import { FilterByPipe } from './pipe/filter-by.pipe';
import { SearchPipe } from './pipe/search.pipe';
import { UpTimePipe } from './pipe/up-time.pipe';
import { SpeedPipe, UnitLengthPipe } from './pipe/speed.pipe';



@NgModule({
  declarations: [FilterByPipe, UnitLengthPipe, SpeedPipe, SearchPipe, UpTimePipe],
  imports: [],
  exports: [FilterByPipe, UnitLengthPipe, SpeedPipe, SearchPipe, UpTimePipe]
})
export class CoreApiModule { }
