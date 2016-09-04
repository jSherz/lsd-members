/* tslint:disable:no-unused-variable */

import {
  inject, TestBed
} from '@angular/core/testing';
import { HttpModule } from '@angular/http';

import { SignupService, SignupServiceImpl } from './signup.service';

describe('Signup Service', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [{ provides: SignupService, useClass: SignupServiceImpl }]
    });
  });

  it('should ...', inject([SignupService], (service: SignupService) => {
    expect(service).toBeTruthy();
  }));

});
