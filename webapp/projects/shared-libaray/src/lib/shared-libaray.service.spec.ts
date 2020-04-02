import { TestBed } from '@angular/core/testing';

import { SharedLibarayService } from './shared-libaray.service';

describe('SharedLibarayService', () => {
  let service: SharedLibarayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SharedLibarayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
