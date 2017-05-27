import {TestBed, inject, async} from '@angular/core/testing';
import {HttpModule} from '@angular/http';

import {PackingListService, PackingListServiceImpl} from './packing-list.service';
import {JwtService} from '../login/jwt.service';
import {StubJwtService} from '../login/jwt.service.stub';

describe('PackingListService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: PackingListService, useClass: PackingListServiceImpl},
        {provide: JwtService, useValue: new StubJwtService('asdf')}
      ]
    });
  });

  it('should be created', async(inject([PackingListService], (packingListService: PackingListService) => {
  })));

});
