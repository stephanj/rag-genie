import { TestBed } from '@angular/core/testing';

import { TextSplittingService } from './text-splitting.service';

describe('TextSplittingService', () => {
  let service: TextSplittingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TextSplittingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
