/* tslint:disable:no-unused-variable */

import {
  inject, TestBed
} from '@angular/core/testing';

import { SignupBaseComponent } from './signup-base.component';


describe('Signup Base Component', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SignupBaseComponent]
    });
  });

  it('should create the app', inject([SignupBaseComponent], (app: SignupBaseComponent) => {
    expect(app).toBeTruthy();
  }));

});
