/* tslint:disable:no-unused-variable */

import {async, inject, TestBed} from '@angular/core/testing';

import {JwtServiceImpl, JwtService} from './jwt.service';


describe('Service: Jwt', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: JwtService, useClass: JwtServiceImpl},
      ]
    });
  });

  it('should store the key it\'s given', async(() => {
    const service = new JwtServiceImpl();

    service.setJwt('a.b.c');

    expect(service.getJwt()).toEqual('a.b.c');
  }));

  it('should remove the key when no new key is given', async(() => {
    const service = new JwtServiceImpl();

    service.setJwt('');

    expect(service.getJwt()).toEqual('');
  }));

  it('should not return authenticated when it has no key', async(() => {
    const service = new JwtServiceImpl();

    service.setJwt('');

    expect(service.isAuthenticated()).toEqual(false);
  }));

  it('should return authenticated when it has a key', async(() => {
    const service = new JwtServiceImpl();

    service.setJwt('asdasdasd.asdasdasdasd.asdasdasdasd');

    expect(service.isAuthenticated()).toEqual(true);
  }));

});
