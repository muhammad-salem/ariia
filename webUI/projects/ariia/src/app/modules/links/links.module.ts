import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LinkComponent } from './link/link.component';
import { MetaLinkComponent } from './meta-link/meta-link.component';
import { AddLinkViewerComponent } from './add-link-viewer/add-link-viewer.component';



@NgModule({
  id: 'links',
  declarations: [
    LinkComponent,
    MetaLinkComponent,
    AddLinkViewerComponent
  ],
  imports: [
    CommonModule
  ]
})
export class LinksModule { }
