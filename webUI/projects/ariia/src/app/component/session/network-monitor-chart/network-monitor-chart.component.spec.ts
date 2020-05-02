import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NetworkMonitorChartComponent } from './network-monitor-chart.component';

describe('NetworkMonitorChartComponent', () => {
  let component: NetworkMonitorChartComponent;
  let fixture: ComponentFixture<NetworkMonitorChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NetworkMonitorChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NetworkMonitorChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
