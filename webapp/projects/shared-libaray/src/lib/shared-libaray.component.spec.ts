import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedLibarayComponent } from './shared-libaray.component';

describe('SharedLibarayComponent', () => {
  let component: SharedLibarayComponent;
  let fixture: ComponentFixture<SharedLibarayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SharedLibarayComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SharedLibarayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
