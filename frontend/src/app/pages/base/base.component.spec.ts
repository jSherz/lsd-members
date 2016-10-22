/* tslint:disable:no-unused-variable */

import {Router} from '@angular/router';
import {async, inject, TestBed} from '@angular/core/testing';

import {BaseComponent} from './base.component';
import {TestModule} from '../../test.module';

describe('Component: Base', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule]
    });
  });

  it('should create an instance', async(inject([Router], (router: Router) => {
    let component = new BaseComponent(router);
    expect(component).toBeTruthy();
  })));

});
