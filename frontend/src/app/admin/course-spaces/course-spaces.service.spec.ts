/* tslint:disable:no-unused-variable */

import {inject, async, TestBed} from '@angular/core/testing';
import {HttpModule} from '@angular/http';

import {CourseSpaceService, CourseSpaceServiceImpl} from './course-spaces.service';
import {StubApiKeyService} from '../utils/api-key.service.stub';
import {ApiKeyService} from '../utils/api-key.service';


describe('Service: CourseSpaces', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: ApiKeyService, useValue: new StubApiKeyService('12345')},
        {provide: CourseSpaceService, useClass: CourseSpaceServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([CourseSpaceService], (service: CourseSpaceService) => {
    expect(service).toBeTruthy();
  })));

});
