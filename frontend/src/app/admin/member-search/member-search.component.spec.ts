/* tslint:disable:no-unused-variable */

import { Http, HttpModule } from '@angular/http';
import { TestBed, inject  } from '@angular/core/testing/test_bed';

import { MemberSearchComponent } from './member-search.component';
import { MemberSearchService, MemberSearchServiceImpl } from './member-search.service';

describe('Component: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        { provide: MemberSearchService, useClass: MemberSearchServiceImpl },
        MemberSearchComponent
      ]
    });
  });

  it('should create an instance', inject([MemberSearchComponent], (component: MemberSearchComponent) => {
    expect(component).toBeTruthy();
  }));

});
