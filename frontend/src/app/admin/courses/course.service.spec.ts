/* tslint:disable:no-unused-variable */

import {CourseService, CourseServiceImpl} from './course.service';
import {addProviders} from '@angular/core/testing/testing';
import {inject} from '@angular/core/testing/test_injector';
import {HTTP_PROVIDERS} from '@angular/http';

describe('Course Service', () => {
  beforeEach(() => addProviders([HTTP_PROVIDERS, CourseServiceImpl]));

  it('should ...',
      inject([CourseServiceImpl], (service: CourseService) => {
    expect(service).toBeTruthy();
  }));
});
