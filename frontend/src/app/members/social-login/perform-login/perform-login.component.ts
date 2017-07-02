import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {SocialLoginService} from '../social-login.service';
import {JwtService} from '../../login/jwt.service';
import {SocialLoginResponse} from '../model';

@Component({
  selector: 'lsd-perform-login',
  templateUrl: './perform-login.component.html',
  styleUrls: ['./perform-login.component.sass']
})
export class PerformLoginComponent implements OnInit {

  loginFailed = false;

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private service: SocialLoginService,
              private jwtService: JwtService) {
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe((params: Params) => {
      if (params && params.code) {
        this.service.login(params.code).subscribe(this.handleResponse, this.handleError);
      } else {
        this.router.navigate(['members']);
      }
    });
  }

  private handleResponse = (response: SocialLoginResponse) => {
    if (response.success) {
      this.jwtService.setJwt(response.jwt, response.committeeMember);

      if (response.committeeMember) {
        this.router.navigate(['members', 'committee', 'dashboard']);
      } else {
        this.router.navigate(['members', 'dashboard']);
      }
    } else {
      console.error('Failed to perform social login:', response.error);
      this.loginFailed = true;
    }
  }

  private handleError = (error) => {
    console.error('Failed to perform social login:', error);
    this.loginFailed = true;
  }

}
