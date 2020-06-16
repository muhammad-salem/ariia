import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ItemViewComponent } from './item-view/item-view.component';
import { ItemListComponent } from './item-list/item-list.component';
import { ItemTableComponent } from './item-table/item-table.component';
import { Routes, RouterModule } from '@angular/router';
import { DownloadViewerComponent } from './download-viewer/download-viewer.component';
import { MatTableModule } from '@angular/material/table';
import { CoreApiModule } from 'core-api';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { FlexLayoutModule } from '@angular/flex-layout';

const routes: Routes = [
  {
    path: '', component: DownloadViewerComponent, pathMatch: 'full'
  }
];

@NgModule({
  declarations: [
    ItemViewComponent,
    ItemListComponent,
    ItemTableComponent,
    DownloadViewerComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    CoreApiModule,
    MatTableModule,
    MatProgressBarModule,
    MatCheckboxModule,
    MatTabsModule,
    MatIconModule,
    MatCardModule,
    MatGridListModule,
    FlexLayoutModule
  ],
  exports: [
    ItemViewComponent,
    ItemListComponent,
    ItemTableComponent,
    DownloadViewerComponent
  ],
  bootstrap: []
})
export class DownloadsModule { }
