/* tslint:disable:no-unused-variable */

import { addProviders, async, inject } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing/test_bed';

import { ApiKeyService, ApiKeyServiceImpl } from './api-key.service';
import { HttpModule } from '@angular/http';

describe('Service: ApiKey', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: ApiKeyService, useClass: ApiKeyServiceImpl}
      ]
    });
  });

  it('should store the key it\'s given', inject([ApiKeyService], (service: ApiKeyService) => {
    service.setKey('48db70de-9a39-4211-ae97-f1d97976aab2');

    expect(service.getKey()).toEqual('48db70de-9a39-4211-ae97-f1d97976aab2');
  }));

  it('should remove the key when no new key is given', inject([ApiKeyService], (service: ApiKeyService) => {
    service.setKey('');

    expect(service.getKey()).toEqual('');
  }));

  it('should not return authenticated when it has no key', inject([ApiKeyService], (service: ApiKeyService) => {
    service.setKey('');

    expect(service.isAuthenticated()).toEqual(false);
  }));

  it('should return authenticated when it has a key', inject([ApiKeyService], (service: ApiKeyService) => {
    service.setKey('88bc0729-9de1-4091-b79a-0a20b96122d1');

    expect(service.isAuthenticated()).toEqual(true);
  }));

});
