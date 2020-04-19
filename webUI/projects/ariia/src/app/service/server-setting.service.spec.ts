import { TestBed } from '@angular/core/testing';

import { ServerSettingService } from './server-setting.service';

describe('ServerSettingService', () => {
  let service: ServerSettingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerSettingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
