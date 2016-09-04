import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BaseService, ApiKeyService } from '../../utils';

export class SignupResult {

  success: boolean;
  errors: any;

  constructor(success: boolean, errors: any) {
    this.success = success;
    this.errors = errors;
  }

}

export abstract class SignupService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract signup(name: string, phoneNumber?: string): Observable<SignupResult>;

  abstract signupAlt(name: string, email?: string): Observable<SignupResult>;

}

@Injectable()
export class SignupServiceImpl extends SignupService {

  private signupUrl: string = 'http://localhost:8080/api/v1/members/sign-up';
  private signupAltUrl: string = 'http://localhost:8080/api/v1/members/sign-up/alt';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  signup(name: string, phoneNumber: string): Observable<SignupResult> {
    let request = {
      name: name,
      phoneNumber: phoneNumber
    };

    return this.doSignup(this.signupUrl, request);
  }

  signupAlt(name: string, email?: string): Observable<SignupResult> {
    let request = {
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
