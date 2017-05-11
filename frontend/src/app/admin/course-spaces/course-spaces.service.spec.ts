/* tslint:disable:no-unused-variable */

import {inject, async, TestBed} from '@angular/core/testing';
import {HttpModule} from '@angular/http';

import {CourseSpaceService, CourseSpaceServiceImpl} from './course-spaces.service';
import {API_KEY, StubApiKeyService} from '../utils/api-key.service.stub';
import {ApiKeyService} from '../utils/api-key.service';


describe('Service: CourseSpaces', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: API_KEY, useValue: '12345'},
        {provide: ApiKeyService, useClass: StubApiKeyService},
        {provide: CourseSpaceService, useClass: CourseSpaceServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([CourseSpaceService], (service: CourseSpaceService) => {
    expect(service).toBeTruthy();
  })));

});
