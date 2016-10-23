/* tslint:disable:no-unused-variable */

import {inject, async, TestBed} from '@angular/core/testing';
import {HttpModule} from '@angular/http';

import {CommitteeService, CommitteeServiceImpl} from './committee.service';
import {ApiKeyService} from '../utils/api-key.service';
import {StubApiKeyService} from '../utils/api-key.service.stub';


describe('Service: Committee', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: ApiKeyService, useValue: new StubApiKeyService('12345')},
        {provide: CommitteeService, useClass: CommitteeServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([CommitteeService], (service: CommitteeService) => {
    expect(service).toBeTruthy();
  })));

});
