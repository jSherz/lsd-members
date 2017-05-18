/* tslint:disable:no-unused-variable */

import {async, TestBed} from '@angular/core/testing';
import {LoginComponent} from './';
import {SocialLoginService, SocialLoginServiceStub} from '../social-login';
import {JwtLoginService} from './jwt-login.service';
import {JwtLoginServiceStub} from './jwt-login.service.stub';

describe('Component: Members - Login', () => {

  let socialLoginService: SocialLoginServiceStub = null;

  beforeEach(() => {
    socialLoginService = new SocialLoginServiceStub();

    TestBed.configureTestingModule({
      declarations: [
        LoginComponent
      ],
      providers: [
        {provide: SocialLoginService, useValue: socialLoginService},
        {provide: JwtLoginService, useClass: JwtLoginServiceStub},
      ]
    }).compileComponents();
  });

  it('should retrieve the login URL', async(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    expect(component.loginUrl).toEqual('http://localhost/some/login/url');
    expect(component.loginUnavailable).toEqual(false);
  }));

  it('should show an error if getting the login URl fails', async(() => {
    socialLoginService.failGetLoginUrlRequest = true;

    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    expect(component.loginUrl).toEqual(null);
    expect(component.loginUnavailable).toEqual(true);
  }));

  // function makeActivatedRoute(params: Params): ActivatedRoute {
  //   return {
  //     queryParams: Observable.of(params)
  //   } as ActivatedRoute;
  // }

  // it('should navigate to the dashboard if the login succeeds', async(() => {
  //   const fixture = TestBed.createComponent(LoginComponent);
  //   const component = fixture.componentInstance;
  //
  //   socialLoginService.failGetLoginUrlRequest = false;
  //   socialLoginService.failLoginRequest = false;
  //
  //   // Ensure the test router has a navigate method (missing when using RouterTestingModule)
  //   (<any>component).router = {
  //     navigate: (_) => null
  //   };
  //
  //   const navSpy = spyOn((<any>component).router, 'navigate');
  //
  //   component.login();
  //
  //   fixture.whenStable().then(() => {
  //     expect(navSpy).toHaveBeenCalledWith(['members', 'dashboard']);
  //   });
  // }));
  //
  // it('should show an error if the login fails', async(() => {
  //   const fixture = TestBed.createComponent(LoginComponent);
  //   const component = fixture.componentInstance;
  //
  //   socialLoginService.failLogin = true;
  //
  //   expect(component.loginFailed).toBe(false);
  //
  //   component.login();
  //
  //   fixture.whenStable().then(() => {
  //     expect(component.loginFailed).toBe(true);
  //   });
  // }));
  //
  // it('should show an error if the login request fails', async(() => {
  //   const fixture = TestBed.createComponent(LoginComponent);
  //   const component = fixture.componentInstance;
  //
  //   socialLoginService.failLogin = false;
  //   socialLoginService.failLoginRequest = true;
  //
  //   expect(component.loginFailed).toBe(false);
  //
  //   component.login();
  //
  //   fixture.whenStable().then(() => {
  //     expect(component.loginFailed).toBe(true);
  //   });
  // }));

});
