import {Http} from '@angular/http';
import {Inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {BaseService} from '../utils/base.service';
import {LoginResult} from './login-result';
import {environment} from '../../../environments/environment';
import {JwtService} from './jwt.service';
import {APP_VERSION} from '../../app.module';

export abstract class JwtLoginService extends BaseService {

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract login(signedFbRequest: String): Observable<LoginResult>;

}

@Injectable()
export class JwtLoginServiceImpl extends JwtLoginService {

  loginUrl = environment.apiUrl + '/api/v1/social-login';

  constructor(http: Http, jwtService: JwtService, @Inject(APP_VERSION) appVersion: string) {
    super(http, jwtService, appVersion);
  }

  login(signedRequest: String): Observable<LoginResult> {
    return this.post(this.loginUrl, {signedRequest})
      .map(r => r.json() as LoginResult)
      .map((result: LoginResult) => {
        if (result.success) {
          this.jwtService.setJwt(result.jwt, result.committeeMember);
        } else {
          this.jwtService.setJwt('', false);
        }

        return result;
      });
  }

}
