import {Inject, Injectable} from '@angular/core';
import {Http} from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

import {SignupResult} from './signup-result';
import {BaseService} from '../../../members/utils/base.service';
import {environment} from '../../../../environments/environment';
import {APP_VERSION} from '../../../app.module';
import {JwtService} from '../../../members/login/jwt.service';


export abstract class SignupService extends BaseService {

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract signup(name: string, phoneNumber?: string): Observable<SignupResult>;

  abstract signupAlt(name: string, email?: string): Observable<SignupResult>;

}

@Injectable()
export class SignupServiceImpl extends SignupService {

  private signupUrl = environment.apiUrl + '/api/v1/members/sign-up';
  private signupAltUrl = environment.apiUrl + '/api/v1/members/sign-up/alt';

  constructor(http: Http, jwtService: JwtService, @Inject(APP_VERSION) appVersion: string) {
    super(http, jwtService, appVersion);
  }

  signup(name: string, phoneNumber: string): Observable<SignupResult> {
    const request = {
      name: name,
      phoneNumber: phoneNumber
    };

    return this.doSignup(this.signupUrl, request);
  }

  signupAlt(name: string, email?: string): Observable<SignupResult> {
    const request = {
      name: name,
      email: email
    };

    return this.doSignup(this.signupAltUrl, request);
  }

  private doSignup(url: string, request: any): Observable<SignupResult> {
    return this.post(url, request)
      .map(r => r.json() as SignupResult);
  }

}
