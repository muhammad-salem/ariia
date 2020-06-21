import {Component, EventEmitter, OnInit, Output} from '@angular/core';

@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

	@Output() toggleSideBar: EventEmitter<any> = new EventEmitter<any>();

	constructor() {
	}

	ngOnInit(): void {
	}

	toggleBar() {
		this.toggleSideBar.emit();
	}

}
