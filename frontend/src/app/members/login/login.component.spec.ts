/* tslint:disable:no-unused-variable */

import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {async, ComponentFixture, inject, TestBed, fakeAsync, tick} from '@angular/core/testing';
import {LoginComponent} from './';
import {RouterTestingModule} from '@angular/router/testing';
import {SocialLoginService, SocialLoginServiceStub} from '../social-login';
import {FacebookModule} from 'ngx-facebook';
import {Router} from '@angular/router';
import {JwtLoginService, JwtLoginServiceImpl} from './jwt-login.service';
import {JwtLoginServiceStub} from './jwt-login.service.stub';

describe('Component: Members - Login', () => {

  const socialLoginService = new SocialLoginServiceStub();

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        LoginComponent
      ],
      imports: [
        RouterTestingModule
      ],
      providers: [
        {provide: SocialLoginService, useValue: socialLoginService},
        {provide: JwtLoginService, useClass: JwtLoginServiceStub}
      ]
    }).compileComponents();
  });

  it('should navigate to the dashboard if the login succeeds', async(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    socialLoginService.loginResponseToReturn = {
      authResponse: {
        accessToken: '11223344',
        expiresIn: 5,
        signedRequest: '123456',
        userID: '88888888',
      },
      status: 'connected'
    };

    // Ensure the test router has a navigate method (missing when using RouterTestingModule)
    (<any>component).router = {
      navigate: (_) => null
    };

    const navSpy = spyOn((<any>component).router, 'navigate');

    component.login();

    fixture.whenStable().then(() => {
      expect(navSpy).toHaveBeenCalledWith(['members', 'dashboard']);
    });
  }));

  it('should show an error if the login result is unauthorised', async(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    socialLoginService.loginResponseToReturn = {
      authResponse: null,
      status: 'unauthorized'
    };

    expect(component.loginFailed).toBe(false);

    component.login();

    fixture.whenStable().then(() => {
      expect(component.loginFailed).toBe(true);
    });
  }));

  it('should show an error if the login result is not_authorized', async(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    socialLoginService.loginResponseToReturn = {
      authResponse: null,
      status: 'not_authorized'
    };

    expect(component.loginFailed).toBe(false);

    component.login();

    fixture.whenStable().then(() => {
      expect(component.loginFailed).toBe(true);
    });
  }));

  it('should show an error when the login fails completely', fakeAsync(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    socialLoginService.failLogin = true;

    expect(component.loginFailed).toBe(false);

    component.login();

    tick();
    expect(component.loginFailed).toBe(true);
  }));

});
