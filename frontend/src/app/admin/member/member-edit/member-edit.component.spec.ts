/* tslint:disable:no-unused-variable */

import {TestBed, async, inject} from '@angular/core/testing';
import {MemberEditComponent} from './member-edit.component';

import {TestModule} from '../../../test.module';


describe('Component: MemberEdit', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [MemberEditComponent]
    });
  });

  it('should create an instance', async(inject([MemberEditComponent], (component: MemberEditComponent) => {
    expect(component).toBeTruthy();
  })));

});
