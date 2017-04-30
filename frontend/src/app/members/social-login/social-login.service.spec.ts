import {TestBed, inject} from '@angular/core/testing';

import {SocialLoginService, SocialLoginServiceImpl} from './social-login.service';
import {FacebookService} from 'ngx-facebook';

describe('SocialLoginService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: FacebookService, useValue: {
          init: (values: any) => {},
          login: () => {
            return {
              status: 'connected'
            };
          }
        }},
        {provide: SocialLoginService, useClass: SocialLoginServiceImpl}
      ]
    });
  });

  it('should ...', inject([SocialLoginService], (service: SocialLoginService) => {
    expect(service).toBeTruthy();
  }));
});
