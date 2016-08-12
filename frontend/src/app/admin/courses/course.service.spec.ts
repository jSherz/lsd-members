/* tslint:disable:no-unused-variable */

import {CourseService, CourseServiceImpl} from './course.service';
import {addProviders} from "@angular/core/testing/testing";
import {inject} from "@angular/core/testing/test_injector";

describe('Course Service', () => {
  beforeEach(() => addProviders([CourseServiceImpl]));

  it('should ...',
      inject([CourseServiceImpl], (service: CourseService) => {
    expect(service).toBeTruthy();
  }));
});
