import {Http} from '@angular/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {BaseService} from '../utils/base.service';
import {LoginResult} from './login-result';
import {environment} from '../../../environments/environment';

export abstract class JwtLoginService extends BaseService {

  constructor(http: Http) {
    super(http);
  }

  abstract login(signedFbRequest: String): Observable<LoginResult>;

}

@Injectable()
export class JwtLoginServiceImpl extends JwtLoginService {

  loginUrl = environment.apiUrl + '/api/v1/social-login';

  constructor(http: Http) {
    super(http);
  }

  login(signedRequest: String): Observable<LoginResult> {
    return this.post(this.loginUrl, {signedRequest})
      .map(r => this.extractJson<LoginResult>(r))
      .catch(this.handleError());
  }

}
