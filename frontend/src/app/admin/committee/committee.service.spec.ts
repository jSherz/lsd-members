/* tslint:disable:no-unused-variable */

import {inject, async, TestBed} from '@angular/core/testing';
import {Http, Response, ResponseOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {CommitteeService, CommitteeServiceImpl} from './committee.service';
import {ApiKeyService} from '../utils/api-key.service';
import {API_KEY, StubApiKeyService} from '../utils/api-key.service.stub';


describe('Service: Committee', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: Http, useValue: {
          get(url) {
            return Observable.of(new Response(new ResponseOptions({
              body: '[{"uuid": "ade89632-2ccd-4b9f-93f1-44f5adf9704c", "name": "Ann Bass"},' +
              '{"uuid": "329ad322-fe4a-4bdc-9c5b-030431766e36", "name": "Anna Morales"},' +
              '{"uuid": "a3fb1c86-af9c-49db-a0ac-3bff22b9352d", "name": "Anthony Smith"}]'
            })));
          }
        }
        },
        {provide: API_KEY, useValue: '487a2930-8e6a-41a5-bcc0-b7fd7f2421e4'},
        {provide: ApiKeyService, useClass: StubApiKeyService},
        {provide: CommitteeService, useClass: CommitteeServiceImpl}
      ]
    });
  });

  it('should return the correct active committee', async(inject([CommitteeService], (service: CommitteeService) => {
    service.active().subscribe(result => {
      expect(result).toEqual(
        [
          {
            'uuid': 'ade89632-2ccd-4b9f-93f1-44f5adf9704c',
            'name': 'Ann Bass'
          },
          {
            'uuid': '329ad322-fe4a-4bdc-9c5b-030431766e36',
            'name': 'Anna Morales'
          },
          {
            'uuid': 'a3fb1c86-af9c-49db-a0ac-3bff22b9352d',
            'name': 'Anthony Smith'
          }
        ]
      );
    });
  })));

});
