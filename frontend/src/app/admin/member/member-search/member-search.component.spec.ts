/* tslint:disable:no-unused-variable */

import {HttpModule} from '@angular/http';
import {TestBed, inject} from '@angular/core/testing/test_bed';

import {MemberSearchComponent} from './member-search.component';
import {MemberService, MemberServiceImpl} from '../member.service';

describe('Component: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: MemberService, useClass: MemberServiceImpl},
        MemberSearchComponent
      ]
    });
  });

  it('should create an instance', inject([MemberSearchComponent], (component: MemberSearchComponent) => {
    expect(component).toBeTruthy();
  }));

});
