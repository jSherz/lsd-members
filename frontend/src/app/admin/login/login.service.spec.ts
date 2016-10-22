/* tslint:disable:no-unused-variable */

import {async, inject, TestBed} from '@angular/core/testing';

import {LoginService, LoginServiceImpl} from './login.service';
import {TestModule} from '../../test.module';
import {ApiKeyService} from '../utils/api-key.service';
import {StubApiKeyService} from '../utils/api-key.service.stub';


describe('Service: Login', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {provide: ApiKeyService, useValue: new StubApiKeyService('12345')},
        {provide: LoginService, useClass: LoginServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([LoginService], (service: LoginService) => {
    expect(service).toBeTruthy();
  })));

});
