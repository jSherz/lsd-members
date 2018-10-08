import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {Http} from '@angular/http';

import {LoginResult} from './login-result';
import {ApiKeyService, BaseService} from '../utils';
import {environment} from '../../../environments/environment';
import { catchError, map } from 'rxjs/operators';


export abstract class LoginService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract login(email: string, password: string): Observable<LoginResult>;

}

@Injectable()
export class LoginServiceImpl extends LoginService {

  loginUrl = environment.apiUrl + '/api/v1/login';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  login(email: string, password: string): Observable<LoginResult> {
    const body = {
      email: email,
      password: password
    };

    return this.post(this.loginUrl, body)
      .pipe(
        map(r => this.extractJson<LoginResult>(r)),
        catchError(this.handleError())
      );
  }

}
