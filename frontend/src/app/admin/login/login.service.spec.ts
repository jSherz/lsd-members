/* tslint:disable:no-unused-variable */

import { async, inject, TestBed } from '@angular/core/testing';
import { HttpModule } from '@angular/http';

import { LoginService, LoginServiceImpl } from './login.service';

describe('Service: Login', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [{ provides: LoginService, useClass: LoginServiceImpl }]
    });
  });

  it('should ...', inject([LoginService], (service: LoginService) => {
    expect(service).toBeTruthy();
  }));

});
