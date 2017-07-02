import {TestBed, inject, async} from '@angular/core/testing';

import {SocialLoginService, SocialLoginServiceImpl} from './social-login.service';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';

import {SocialLoginUrlResponse} from './model';

describe('SocialLoginService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: SocialLoginService, useClass: SocialLoginServiceImpl},
        {
          provide: Http, useValue: {
          get: (url) => {
            if (url.endsWith('/url')) {
              return Observable.of({
                json: () => new SocialLoginUrlResponse('http://example.com')
              })
            } else {
              throw new Error('Unknown URL used with stubbed HTTP');
            }
          }
        }
        }
      ]
    });
  });

  it('should return the social login URL', async(inject([SocialLoginService], (service: SocialLoginService) => {
    service.getLoginUrl().subscribe((response) => {
      expect(response.url).toEqual('http://example.com');
    });
  })));

});
