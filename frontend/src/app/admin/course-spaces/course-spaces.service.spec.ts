/* tslint:disable:no-unused-variable */

import {inject} from '@angular/core/testing';
import {TestBed} from '@angular/core/testing/test_bed';

import {CourseSpaceService, CourseSpaceServiceImpl} from './course-spaces.service';
import {TestModule} from '../../test.module';

describe('Service: CourseSpaces', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [{provide: CourseSpaceService, useClass: CourseSpaceServiceImpl}]
    });
  });

  it('should ...', inject([CourseSpaceService], (service: CourseSpaceService) => {
    expect(service).toBeTruthy();
  }));

});
