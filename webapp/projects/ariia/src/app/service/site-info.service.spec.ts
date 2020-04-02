import { TestBed } from '@angular/core/testing';

import { SiteInfoService } from './site-info.service';

describe('SiteInfoService', () => {
  let service: SiteInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SiteInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
