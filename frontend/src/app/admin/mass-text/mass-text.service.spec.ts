/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import {HttpModule} from '@angular/http';
import {RouterTestingModule} from '@angular/router/testing';
import {Observable} from 'rxjs';

import { MassTextService, MassTextServiceImpl } from './mass-text.service';
import {ApiKeyService} from '../utils/api-key.service';
import {StubApiKeyService} from '../utils/api-key.service.stub';

describe('Service: MassText', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: MassTextService, useClass: MassTextServiceImpl},
        {provide: ApiKeyService, useValue: new StubApiKeyService('487a2930-8e6a-41a5-bcc0-b7fd7f2421e4')}
      ]
    });
  });

  it('should be creatable', inject([MassTextService], (service: MassTextService) => {
    expect(service).toBeTruthy();
  }));
});
