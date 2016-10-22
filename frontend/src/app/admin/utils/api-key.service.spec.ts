/* tslint:disable:no-unused-variable */

import {async, inject, TestBed} from '@angular/core/testing';

import {ApiKeyServiceImpl, ApiKeyService} from './api-key.service';


describe('Service: ApiKey', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: ApiKeyService, useClass: ApiKeyServiceImpl},
      ]
    });
  });

  it('should store the key it\'s given', async(() => {
    let service = new ApiKeyServiceImpl();

    service.setKey('48db70de-9a39-4211-ae97-f1d97976aab2');

    expect(service.getKey()).toEqual('48db70de-9a39-4211-ae97-f1d97976aab2');
  }));

  it('should remove the key when no new key is given', async(() => {
    let service = new ApiKeyServiceImpl();

    service.setKey('');

    expect(service.getKey()).toEqual('');
  }));

  it('should not return authenticated when it has no key', async(() => {
    let service = new ApiKeyServiceImpl();

    service.setKey('');

    expect(service.isAuthenticated()).toEqual(false);
  }));

  it('should return authenticated when it has a key', async(() => {
    let service = new ApiKeyServiceImpl();

    service.setKey('88bc0729-9de1-4091-b79a-0a20b96122d1');

    expect(service.isAuthenticated()).toEqual(true);
  }));

});
