import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkMonitorComponent } from './network-monitor.component';

describe('NetworkMonitorComponent', () => {
  let component: NetworkMonitorComponent;
  let fixture: ComponentFixture<NetworkMonitorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkMonitorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
