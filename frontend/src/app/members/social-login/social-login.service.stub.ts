import {SocialLoginService} from './social-login.service';
import {LoginResponse} from 'ngx-facebook';

export class SocialLoginServiceStub implements SocialLoginService {

  loginResponseToReturn: LoginResponse = {
    status: 'unauthorized',
    authResponse: null
  };

  failLogin = false;

  login(): Promise<LoginResponse> {
    if (this.failLogin) {
      return new Promise((resolve, reject) => {
        reject('Simulating login completely failing.');
      });
    } else {
      return Promise.resolve(this.loginResponseToReturn);
    }
  }

}
