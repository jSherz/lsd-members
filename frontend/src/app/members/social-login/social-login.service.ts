import {Injectable} from '@angular/core';
import {Http} from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {environment} from '../../../environments/environment';
import {SocialLoginRequest, SocialLoginResponse, SocialLoginUrlResponse} from './model';

export abstract class SocialLoginService {

  abstract getLoginUrl(): Observable<SocialLoginUrlResponse>;

  abstract login(verificationCode: string): Observable<SocialLoginResponse>;

}

@Injectable()
export class SocialLoginServiceImpl implements SocialLoginService {

  private http: Http;

  private baseEndpoint: string = environment.apiUrl + '/api/v1/social-login';
  private getLoginUrlEndpoint: string = this.baseEndpoint + '/url';
  private loginEndpoint: string = this.baseEndpoint + '/verify';

  constructor(http: Http) {
    this.http = http;
  }

  getLoginUrl(): Observable<SocialLoginUrlResponse> {
    return this.http.get(this.getLoginUrlEndpoint)
      .map(r => r.json() as SocialLoginUrlResponse);
  }

  login(verificationCode: string): Observable<SocialLoginResponse> {
    const request = new SocialLoginRequest(verificationCode);

    return this.http.post(this.loginEndpoint, request)
      .map(r => r.json() as SocialLoginResponse);
  }

}
