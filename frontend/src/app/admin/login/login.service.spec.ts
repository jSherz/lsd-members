/* tslint:disable:no-unused-variable */

import {async, inject, TestBed} from '@angular/core/testing';

import {LoginService, LoginServiceImpl} from './login.service';
import {TestModule} from '../../test.module';
import {ApiKeyService} from '../utils/api-key.service';
import {API_KEY, StubApiKeyService} from '../utils/api-key.service.stub';


describe('Service: Login', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {provide: API_KEY, useValue: '12345'},
        {provide: ApiKeyService, useClass: StubApiKeyService},
        {provide: LoginService, useClass: LoginServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([LoginService], (service: LoginService) => {
    expect(service).toBeTruthy();
  })));

});
