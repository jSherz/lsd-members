import {TestBed, inject} from '@angular/core/testing';

import {SocialLoginService, SocialLoginServiceImpl} from './social-login.service';
import {Http} from '@angular/http';

describe('SocialLoginService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: SocialLoginService, useClass: SocialLoginServiceImpl},
        {provide: Http, useValue: {}}
      ]
    });
  });

  it('should ...', inject([SocialLoginService], (service: SocialLoginService) => {
    expect(service).toBeTruthy();
  }));
});
