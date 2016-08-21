/* tslint:disable:no-unused-variable */

import { inject  } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing/test_bed';

import { CourseSpaceService } from './course-spaces.service';

describe('Service: CourseSpaces', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CourseSpaceService]
    });
  });

  it('should ...', inject([CourseSpaceService], (service: CourseSpaceService) => {
    expect(service).toBeTruthy();
  }));

});
