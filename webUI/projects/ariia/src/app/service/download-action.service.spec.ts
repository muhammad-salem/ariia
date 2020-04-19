import { TestBed } from '@angular/core/testing';

import { DownloadActionService } from './download-action.service';

describe('DownloadActionService', () => {
  let service: DownloadActionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DownloadActionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
