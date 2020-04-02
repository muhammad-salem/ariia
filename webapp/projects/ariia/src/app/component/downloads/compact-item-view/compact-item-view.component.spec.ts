import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompactItemViewComponent } from './compact-item-view.component';

describe('CompactItemViewComponent', () => {
  let component: CompactItemViewComponent;
  let fixture: ComponentFixture<CompactItemViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompactItemViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompactItemViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
