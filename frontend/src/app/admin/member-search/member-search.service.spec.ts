/* tslint:disable:no-unused-variable */

import { inject     } from '@angular/core/testing';
import { TestBed    } from '@angular/core/testing/test_bed';
import { HttpModule } from '@angular/http';

import { MemberSearchService, MemberSearchServiceImpl } from './member-search.service';

describe('Service: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        { provide: MemberSearchService, useClass: MemberSearchServiceImpl }
      ]
    });
  });

  it('should ...', inject([MemberSearchService], (service: MemberSearchService) => {
    expect(service).toBeTruthy();
  }));

});
