/* tslint:disable:no-unused-variable */

import {inject, TestBed, async} from '@angular/core/testing';
import {HttpModule} from '@angular/http';

import {CourseService, CourseServiceImpl} from './course.service';
import {API_KEY, StubApiKeyService} from '../utils/api-key.service.stub';
import {ApiKeyService} from '../utils/api-key.service';


describe('Course Service', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: API_KEY, useValue: '12345'},
        {provide: ApiKeyService, useClass: StubApiKeyService},
        {provide: CourseService, useClass: CourseServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([CourseService], (service: CourseService) => {
    expect(service).toBeTruthy();
  })));

});
