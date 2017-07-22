/* tslint:disable:no-unused-variable */

import {TestBed, inject, async} from '@angular/core/testing';

import {SocialLoginService, SocialLoginServiceImpl} from './social-login.service';
import {Http, Response, ResponseOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import {SocialLoginRequest, SocialLoginResponse} from './model';

describe('SocialLoginService', () => {

  it('makes a login request and parses the response', () => {
    const http: any = {
      post: () => {
        return Observable.of(new Response(new ResponseOptions({
          status: 200,
          body: JSON.stringify(new SocialLoginResponse(true, null, 'jwt.1.2', false)),
          url: 'https://myspace.com'
        })));
      }
    };
    const postSpy = spyOn(http, 'post').and.callThrough();

    const service: SocialLoginService = new SocialLoginServiceImpl(http);

    expect(postSpy).not.toHaveBeenCalled();

    const response = service.login('235y8werin285wefsdf23');
    response.subscribe((result) => {
      expect(result.success).toBeTruthy();
      expect(result.error).toBeNull();
      expect(result.jwt).toEqual('jwt.1.2');
      expect(result.committeeMember).toBeFalsy();
    });

    expect(postSpy.calls.count()).toEqual(1);

    const call = postSpy.calls.first();
    const url = call.args[0];
    const body = call.args[1];

    expect(url).toEqual('https://api.dev.leedsskydivers.com/api/v1/social-login/verify');
    expect(body).toEqual(new SocialLoginRequest('235y8werin285wefsdf23'));
  });

});
