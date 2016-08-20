/* tslint:disable:no-unused-variable */

import { inject } from '@angular/core/testing';
import { TestBed } from "@angular/core/testing/test_bed";

import { MemberSearchService } from './member-search.service';

describe('Service: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MemberSearchService]
    });
  });

  it('should ...', inject([MemberSearchService], (service: MemberSearchService) => {
    expect(service).toBeTruthy();
  }));

});
