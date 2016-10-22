/* tslint:disable:no-unused-variable */

import {
  inject, TestBed, async
} from '@angular/core/testing';

import {SignupService, SignupServiceImpl} from './signup.service';
import {TestModule} from '../../../test.module';
import {ApiKeyService} from '../../utils/api-key.service';
import {StubApiKeyService} from '../../utils/api-key.service.stub';


describe('Service: Signup', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {provide: ApiKeyService, useValue: new StubApiKeyService('12345')},
        {provide: SignupService, useClass: SignupServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([SignupService], (service: SignupService) => {
    expect(service).toBeTruthy();
  })));

});
