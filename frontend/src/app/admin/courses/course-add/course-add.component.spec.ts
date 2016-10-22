/* tslint:disable:no-unused-variable */

import {TestBed, inject, async} from '@angular/core/testing';

import {CourseAddComponent} from './course-add.component';
import {TestModule} from '../../../test.module';
import {StubCourseService} from '../course-calendar/course-calendar.component.spec';
import {CourseService} from '../course.service';

describe('Component: CourseAddComponent', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {provide: CourseService, useValue: new StubCourseService([])},
        CourseAddComponent
      ]
    });
  });

  it('should create an instance', async(inject([CourseAddComponent], (component: CourseAddComponent) => {
    expect(component).toBeTruthy();
  })));


});
