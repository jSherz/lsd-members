import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';

import {SocialLoginResponse} from './model';
import {SocialLoginService} from './social-login.service';

export class SocialLoginServiceStub implements SocialLoginService {

  failGetLoginUrlRequest = false;

  login(verificationCode: string): Observable<SocialLoginResponse> {
    if (verificationCode === 'FAIL_LOGIN') {
      return Observable.of(new SocialLoginResponse(false, 'Generic login error.', null, false));
    } else if (verificationCode === 'FAIL_LOGIN_REQUEST') {
      return Observable.throw('Social login request failed! :O ERROR 4000009 - NO CATS');
    } else if (verificationCode === 'COMMITTEE') {
      return Observable.of(new SocialLoginResponse(true, null, 'jwt.1.2', true));
    } else {
      return Observable.of(new SocialLoginResponse(true, null, 'jwt.1.2', false));
    }
  }

}
