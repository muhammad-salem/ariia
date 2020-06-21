import {Component, OnInit} from '@angular/core';
import {ItemService} from 'core-api';
import {NotifyService} from '../../../app-root/app-root.component';

@Component({
	selector: 'app-link',
	templateUrl: './link.component.html',
	styleUrls: ['./link.component.scss']
})
export class LinkComponent implements OnInit {

	link: string;
	metaLink: string;
	constructor(private itemService: ItemService, private notifyService: NotifyService) {}

	ngOnInit(): void {}

	addLink() {
		if (this.link === '' && this.metaLink === ''){
			this.notifyService.error('Empty Link', 'Please Provide');
		}
		if (this.link){
			this.itemService.downloadUrl(this.link).subscribe(id => {
				this.notifyService.showSnackBar('Add Download Link',
					this.link + '\nwith id:' + id);
				this.link = '';
			});
		}
		if (this.metaLink){
			const urls = this.metaLink.split('\n');
			this.itemService.createMetaLinkUrl(urls).subscribe(id => {
				this.notifyService.showSnackBar('Add Download Link',
					this.metaLink + '\nwith id:' + id);
				this.metaLink = '';
			});
		}
	}

}
