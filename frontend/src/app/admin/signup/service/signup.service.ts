import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {SignupResult} from './signup-result';
import {ApiKeyService, BaseService} from '../../utils';


export abstract class SignupService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract signup(name: string, phoneNumber?: string): Observable<SignupResult>;

  abstract signupAlt(name: string, email?: string): Observable<SignupResult>;

}

@Injectable()
export class SignupServiceImpl extends SignupService {

  private signupUrl = 'http://localhost:8080/api/v1/members/sign-up';
  private signupAltUrl = 'http://localhost:8080/api/v1/members/sign-up/alt';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
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
      .map(r => this.extractJson<SignupResult>(r))
      .catch(this.handleError());
  }

}
