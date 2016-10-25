/* tslint:disable:no-unused-variable */

import {TestBed, inject, async} from '@angular/core/testing';
import {Router} from '@angular/router';

import {CourseAddComponent} from './course-add.component';
import {TestModule} from '../../../test.module';
import {StubCourseService} from '../course-calendar/course-calendar.component.spec';
import {CourseService} from '../course.service';
import {CourseCreateResponse} from '../model/course-create-response';


describe('Component: CourseAddComponent', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        {
          provide: Router, useValue: {
          navigate(args) {
          }
        }
        },
        {provide: CourseService, useValue: new StubCourseService([])},
        CourseAddComponent
      ]
    });
  });

  it('should show a throbber while the request is running', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.showThrobber).toBeFalsy();

      component.createCourse({
        date: '2017-01-05',
        organiser: '99c1d6dc-3f05-40e9-b842-dbcabc99f5ff',
        numSpaces: StubCourseService.createDontCompleteNumSpaces
      });

      expect(component.showThrobber).toBeTruthy();

      StubCourseService.createDontCompleteSubject.next(
        new CourseCreateResponse(true, null, '2157f47e-6feb-476d-a6fd-29f68421406d')
      );

      expect(component.showThrobber).toBeFalsy();
    })));

  it('should show an error message if the API call fails', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.apiRequestFailed).toBeFalsy();

      component.createCourse({
        date: '2016-10-14',
        organiser: 'c0ed8019-c797-40c8-8590-0fc782a3cec7',
        numSpaces: StubCourseService.createApiErrorNumSpaces
      });

      expect(component.showThrobber).toBeFalsy();
      expect(component.apiRequestFailed).toBeTruthy();
    })));

  it('should show the correct error against number of spaces if it\'s invalid', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.errors).toEqual({});

      component.createCourse({
        date: '2016-11-10',
        organiser: 'fc104988-b6da-4816-8e3c-bbde23b29338',
        numSpaces: StubCourseService.createInvalidNumSpaces
      });

      expect(component.errors).toEqual({
        numSpaces: 'error.invalidNumSpaces'
      });
      expect(component.apiRequestFailed).toBeFalsy();
    })));

  it('should show any other API error (not invalid num spaces) against the general field / key', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.errors).toEqual({});

      component.createCourse({
        date: '2016-11-10',
        organiser: 'fc104988-b6da-4816-8e3c-bbde23b29338',
        numSpaces: StubCourseService.createInvalidMiscErrorNumSpaces
      });

      expect(component.errors).toEqual({
        general: 'error.genericValidationError'
      });
      expect(component.apiRequestFailed).toBeFalsy();
    })));

  it('should translate the invalid number of spaces error correctly', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.translate('error.invalidNumSpaces')).toEqual('Invalid number of spaces - a course must have at ' +
        'least one space and at most 50.');
    })));

  it('should return the translation key if no translation is known', async(inject([CourseAddComponent],
    (component: CourseAddComponent) => {
      expect(component.translate('error.su138rojsdk8y13yrsdff')).toEqual('error.su138rojsdk8y13yrsdff');
    })));

});
