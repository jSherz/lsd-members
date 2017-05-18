import {Observable} from 'rxjs/Observable';

import {SocialLoginResponse, SocialLoginUrlResponse} from './model';
import {SocialLoginService} from './social-login.service';

export class SocialLoginServiceStub implements SocialLoginService {

  failGetLoginUrlRequest = false;

  failLogin = false;
  failLoginRequest = false;

  getLoginUrl(): Observable<SocialLoginUrlResponse> {
    if (this.failGetLoginUrlRequest) {
      return Observable.throw('Social login get URL request failed. TEAPOT EXCEPTION!!!!');
    } else {
      return Observable.of(new SocialLoginUrlResponse('http://localhost/some/login/url'));
    }
  }

  login(verificationCode: string): Observable<SocialLoginResponse> {
    if (this.failLogin) {
      return Observable.of(new SocialLoginResponse(false, 'Generic login error.', null));
    } else if (this.failLoginRequest) {
      return Observable.throw('Social login request failed! :O ERROR 4000009 - NO CATS');
    } else {
      return Observable.of(new SocialLoginResponse(true, null, 'jwt.1.2'));
    }
  }

}
