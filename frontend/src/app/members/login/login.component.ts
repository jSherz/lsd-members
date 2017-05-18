import {Component} from '@angular/core';

import {SocialLoginService} from '../social-login/social-login.service';

@Component({
  selector: 'lsd-members-login',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.sass']
})
export class LoginComponent {

  loginUrl: string = null;

  loginUnavailable = false;

  constructor(private socialLoginService: SocialLoginService) {
    this.socialLoginService.getLoginUrl()
      .subscribe(
        response => {
          this.loginUrl = response.url;
        },
        error => {
          console.error('Failed to get login URL:', error);
          this.loginUnavailable = true;
        }
      );
  }

}
