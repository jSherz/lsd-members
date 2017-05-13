import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {LoginResponse} from 'ngx-facebook';
import {Observable} from 'rxjs/Observable';

import {JwtLoginService} from './jwt-login.service';
import {LoginResult} from './login-result';
import {SocialLoginService} from '../social-login/social-login.service';

@Component({
  selector: 'lsd-members-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.sass']
})
export class LoginComponent {

  loginFailed = false;

  constructor(private socialLoginService: SocialLoginService,
              private loginService: JwtLoginService,
              private router: Router) {
  }

  login() {
    this.socialLoginService.login()
      .then((response) => this.handleLoginResult(response))
      .catch((error: any) => {
        console.error('Login failed - may be as Facebook SDK could not be loaded (e.g. Disconnect or Ghostery' +
          'installed).', error);

        this.loginFailed = true;
      });
  }

  private handleLoginResult(response: LoginResponse) {
    if (response.status === 'connected') {
      this.loginService.login(response.authResponse.signedRequest)
        .subscribe((result: LoginResult) => {
            console.log('JWT: ', result.jwt);

            this.router.navigate(['members', 'dashboard']);
          },
          (error: any) => {
            console.error('App part of login failed.', error);

            this.loginFailed = true;
          });
    } else {
      console.log('Login failed - show error.', response.status);

      this.loginFailed = true;
    }
  }

}
