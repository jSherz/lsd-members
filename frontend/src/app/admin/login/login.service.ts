import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Http       } from '@angular/http';

import { BaseService, ApiKeyService } from '../../utils';


export class LoginResult {

  success: boolean;
  errors: any;
  apiKey: string;

  constructor(success: boolean, errors: any, apiKey: string) {
    this.success = success;
    this.errors = errors;
    this.apiKey = apiKey;
  }

}

export abstract class LoginService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract login(email: string, password: string): Observable<LoginResult>;

}

@Injectable()
export class LoginServiceImpl extends LoginService {

  loginUrl = 'http://localhost:8080/api/v1/login';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  login(email: string, password: string): Observable<LoginResult> {
    let body = {
      email: email,
      password: password
    };

    return this.post(this.loginUrl, body)
      .map(r => this.extractJson<LoginResult>(r))
      .catch(this.handleError());
  }

}
