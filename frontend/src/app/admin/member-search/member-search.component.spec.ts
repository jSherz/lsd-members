/* tslint:disable:no-unused-variable */

import { TestBed, inject } from '@angular/core/testing/test_bed';

import { MemberSearchComponent } from './member-search.component';
import { MemberSearchService   } from './member-search.service';

describe('Component: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MemberSearchService, MemberSearchComponent]
    });
  });

  it('should create an instance', inject([MemberSearchComponent], (component: MemberSearchComponent) => {
    expect(component).toBeTruthy();
  }));

});
