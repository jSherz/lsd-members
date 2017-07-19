/* tslint:disable:no-unused-variable */

import {TestBed, inject, async} from '@angular/core/testing';

import {SocialLoginService, SocialLoginServiceImpl} from './social-login.service';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';

describe('SocialLoginService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: SocialLoginService, useClass: SocialLoginServiceImpl},
        {
          provide: Http, useValue: {}
        }
      ]
    });
  });

});
