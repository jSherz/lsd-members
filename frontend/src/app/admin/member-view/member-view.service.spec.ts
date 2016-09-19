/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { MemberViewService } from './member-view.service';

describe('Service: MemberView', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MemberViewService]
    });
  });

  it('should ...', inject([MemberViewService], (service: MemberViewService) => {
    expect(service).toBeTruthy();
  }));
});
