/* tslint:disable:no-unused-variable */

import {
  ActivatedRoute,
  Params,
  UrlSegment
} from '@angular/router';
import {async} from '@angular/core/testing';
import {Observable, Subject} from 'rxjs/Rx';

import {CourseViewComponent} from './course-view.component';
import {StubCourseService} from '../course.service.stub';
import {StubCourseSpaceService} from '../../course-spaces/course-spaces.service.stub';
import {stubExampleSpaces} from '../model/stub-example-spaces';
import {CourseWithOrganisers, CourseSpaceWithMember} from '../model';


describe('Component: CourseView', () => {

  it('should show a loading indicator while waiting for the course info & spaces', async(() => {
    let setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.next(StubCourseService.getOkCourse);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.next(stubExampleSpaces);

    expect(setup.component.showThrobber).toBeFalsy();
  }));

  it('should show a loading indicator while waiting for the course info & spaces (spaces returns first)', async(() => {
    let setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.next(stubExampleSpaces);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.next(StubCourseService.getOkCourse);

    expect(setup.component.showThrobber).toBeFalsy();
  }));

  it('should hide the throbber if the get spaces request fails', async(() => {
    let setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.error('Simulate course request failing');

    expect(setup.component.showThrobber).toBeFalsy();
  }));

  it('should hide the throbber if the get course request fails', async(() => {
    let setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.error('Simulate course info request failing');

    expect(setup.component.showThrobber).toBeFalsy();
  }));

});

class TestSetup {
  component: CourseViewComponent;
  getCourseSubject: Subject<CourseWithOrganisers>;
  courseSpacesSubject: Subject<CourseSpaceWithMember[]>;

  constructor(component: CourseViewComponent, getCourseSubject: Subject<CourseWithOrganisers>,
              courseSpacesSubject: Subject<CourseSpaceWithMember[]>) {
    this.component = component;
    this.getCourseSubject = getCourseSubject;
    this.courseSpacesSubject = courseSpacesSubject;
  }
}

/**
 * Create a course view component with a stubbed course service.
 *
 * @param courseUuid Course to "display"
 * @returns {CourseViewComponent}
 */
function mockComp(courseUuid: string): TestSetup {
  let params = {uuid: courseUuid};
  let urlParts = [['courses', params]];

  let urls = [new UrlSegment('courses', {'uuid': courseUuid})];

  let observableUrls = Observable.of(urls);
  let observableParams: Observable<Params> = Observable.of(params);

  let activatedRoute = new ActivatedRoute();
  activatedRoute.url = observableUrls;
  activatedRoute.params = observableParams;

  let service = new StubCourseService([]);
  let spaceService = new StubCourseSpaceService();

  let component = new CourseViewComponent(activatedRoute, service, spaceService);
  component.ngOnInit();

  return new TestSetup(component, service.getDontCompleteSubject, service.spacesDontCompleteSubject);
}
