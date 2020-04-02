import { TestBed } from '@angular/core/testing';

import { SiteSettingService } from './site-setting.service';

describe('SiteSettingService', () => {
  let service: SiteSettingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SiteSettingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
