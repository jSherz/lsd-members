/* tslint:disable:no-unused-variable */

import { inject     } from '@angular/core/testing';
import { TestBed    } from '@angular/core/testing/test_bed';
import { HttpModule } from '@angular/http';

import { CourseService, CourseServiceImpl } from './course.service';

describe('Course Service', () => {

  it('should ...', inject([CourseServiceImpl], (service: CourseService) => {
    expect(service).toBeTruthy();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [CourseServiceImpl]
    });
  });

});
