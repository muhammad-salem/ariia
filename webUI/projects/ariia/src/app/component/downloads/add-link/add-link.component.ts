import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ItemService } from '../../../service/item.service';

@Component({
  selector: 'ariia-add-link',
  templateUrl: './add-link.component.html',
  styleUrls: ['./add-link.component.scss']
})
export class AddLinkComponent implements OnInit {

  link: string;
  @Output() newID = new EventEmitter<string>();

  constructor(private itemService: ItemService) {
    this.link = '';
  }

  ngOnInit(): void {
  }

  addLink(){
    this.itemService.downloadUrl(this.link).subscribe(id => {
      this.newID.emit(id);
    });
  }

}
