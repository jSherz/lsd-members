/* tslint:disable:no-unused-variable */

import {async, TestBed} from '@angular/core/testing';
import {LoginComponent} from './';
import {environment} from '../../../environments/environment';

describe('Component: Members - Login', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        LoginComponent
      ]
    }).compileComponents();
  });

  it('should build the correct login URL', async(() => {
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;

    expect(component.loginUrl).toEqual(`https://www.facebook.com/dialog/oauth?client_id=${environment.fbClientId}&` +
      `redirect_uri=${encodeURIComponent(environment.baseUrl)}%2Fmembers%2Fperform-login&` +
      'scope=public_profile%2Cemail');
  }));

});
