import {Component} from '@angular/core';
import {SocialLoginService} from '../social-login/social-login.service';
import {LoginResponse} from 'ngx-facebook';
import {Router} from '@angular/router';

@Component({
  selector: 'lsd-members-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.sass']
})
export class LoginComponent {

  loginFailed = false;

  constructor(private loginService: SocialLoginService,
              private router: Router) {
  }

  login() {
    this.loginService.login()
      .then((response) => this.handleLoginResult(response))
      .catch((error: any) => {
        console.error('Login failed - may be as Facebook SDK could not be loaded (e.g. Disconnect or Ghostery' +
          'installed).', error);

        this.loginFailed = true;
      });
  }

  private handleLoginResult(response: LoginResponse) {
    if (response.status === 'connected') {
      console.log('Logged in with FB.');

      this.router.navigate(['members', 'dashboard']);
    } else {
      console.log('Login failed - show error.', response.status);

      this.loginFailed = true;
    }
  }

}
