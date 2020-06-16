import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CoreApiModule } from 'core-api';
import { LogTableComponent } from './log-table/log-table.component';
import { Routes, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

const routes: Routes = [
  { path: '', component: LogTableComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    LogTableComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    CoreApiModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule
  ],
  bootstrap: []
})
export class LogsModule { }
