import {Injectable} from '@angular/core';
import {FacebookService, InitParams, LoginOptions, LoginResponse} from 'ngx-facebook';

export abstract class SocialLoginService {

  abstract login(): Promise<LoginResponse>;

}

@Injectable()
export class SocialLoginServiceImpl implements SocialLoginService {

  static FB_PARAMS: InitParams = {
    appId: '555160587939138',
    xfbml: true,
    version: 'v2.8'
  };

  static LOGIN_PARAMS: LoginOptions = {
    scope: 'public_profile,email'
  };

  constructor(private fb: FacebookService) {
    fb.init(SocialLoginServiceImpl.FB_PARAMS);
  }

  login(): Promise<LoginResponse> {
    return this.fb.getLoginStatus().then(status => {
      if (status.status === 'connected') {
        return Promise.resolve(status);
      } else {
        return this.fb.login(SocialLoginServiceImpl.LOGIN_PARAMS);
      }
    });
  }

}
