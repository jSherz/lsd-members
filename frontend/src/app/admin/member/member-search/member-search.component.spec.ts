/* tslint:disable:no-unused-variable */

import {TestBed, inject, async} from '@angular/core/testing';

import {MemberSearchComponent} from './member-search.component';
import {TestModule} from '../../../test.module';

describe('Component: MemberSearch', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule]
    });
  });

  it('should create an instance', async(inject([MemberSearchComponent], (component: MemberSearchComponent) => {
    expect(component).toBeTruthy();
  })));

});
