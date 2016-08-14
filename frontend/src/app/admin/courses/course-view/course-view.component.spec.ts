/* tslint:disable:no-unused-variable */

import {
  ActivatedRoute,
  UrlPathWithParams,
  Params
} from '@angular/router';
import { Observable } from 'rxjs/Rx';

import { CourseViewComponent } from './course-view.component';
import { StubCourseService   } from '../course-calendar/course-calendar.component.spec';

describe('Component: CourseView', () => {

  it('should create an instance', () => {
    let component = mockComp('308056ba-81c2-41f0-a06f-00ad162e238c');

    expect(component).toBeTruthy();
  });

});

/**
 * Create a course view component with a stubbed course service.
 *
 * @param courseUuid Course to "display"
 * @returns {CourseViewComponent}
 */
function mockComp(courseUuid: string): CourseViewComponent {
  let params = { uuid: courseUuid };
  let urlParts = [['courses', params]];

  let urls = urlParts.map(([path, pathParams]) => new UrlPathWithParams(path, pathParams));

  let observableUrls = Observable.of(urls);
  let observableParams: Observable<Params> = Observable.of(params);

  let activatedRoute = new ActivatedRoute();
  activatedRoute.url = observableUrls;
  activatedRoute.params = observableParams;

  let service = new StubCourseService([]);
  return new CourseViewComponent(activatedRoute, service);
}
