import {Subject, Observable} from 'rxjs';
import moment = require('moment');

import {CourseService} from './course.service';
import {
  CourseWithOrganisers,
  CourseSpaceWithMember,
  CourseCreateResponse,
  CourseWithNumSpaces,
  CourseCreateRequest
} from './model';
import {stubExampleSpaces} from './model/stub-example-spaces';


/**
 * A fake course service, used for testing.
 */
export class StubCourseService extends CourseService {

  static getDontCompleteSubject = new Subject<CourseWithOrganisers>();
  static getDontCompleteUuid = '692d2968-5600-4e88-9111-1c439e002edf';
  static getApiErrorUuid = '5cb2b7b7-8edb-4e0c-aaa0-a46bf0bc6a81';
  static getOkUuid = '04cf71d9-d709-4f09-af2c-2390cec2b728';

  static spacesDontCompleteSubject = new Subject<CourseSpaceWithMember[]>();
  static spacesDontCompleteUuid = '5a17a150-93b7-4537-8f32-13539f719ea5';
  static spacesApiErrorUuid = '7f03b5bb-ee2c-4879-a3aa-02be3721015f';
  static spacesOkUuid = '04cf71d9-d709-4f09-af2c-2390cec2b728';

  static createDontCompleteSubject = new Subject<CourseCreateResponse>();
  static createDontCompleteNumSpaces = 14;
  static createApiErrorNumSpaces = 11;
  static createOkNumSpaces = 5;
  static createInvalidNumSpaces = 51;
  static createInvalidMiscErrorNumSpaces = 13;

  getByUuid(uuid: string): Observable<CourseWithOrganisers> {
    return undefined;
  }

  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    if (StubCourseService.spacesDontCompleteUuid === uuid) {
      return StubCourseService.spacesDontCompleteSubject;
    } else if (StubCourseService.spacesApiErrorUuid === uuid) {
      return Observable.throw('API error');
    } else if (StubCourseService.spacesOkUuid === uuid) {
      return Observable.of(stubExampleSpaces);
    } else {
      return Observable.throw('Unknown uuid used with stub');
    }
  }

  /**
   * The course service will return the given courses for any find() call.
   *
   * @param courses
   */
  constructor(private courses: CourseWithNumSpaces[]) {
    super(null, null);
  }

  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    return Observable.of(this.courses);
  }

  create(course: CourseCreateRequest): Observable<CourseCreateResponse> {
    if (StubCourseService.createDontCompleteNumSpaces === course.numSpaces) {
      return StubCourseService.createDontCompleteSubject;
    } else if (StubCourseService.createApiErrorNumSpaces === course.numSpaces) {
      return Observable.throw('API error');
    } else if (StubCourseService.createOkNumSpaces === course.numSpaces) {
      return Observable.of(
        new CourseCreateResponse(true, null, '74e24a9f-5fc1-4241-b7d3-c2ebd2c7f61b')
      );
    } else if (StubCourseService.createInvalidNumSpaces === course.numSpaces) {
      return Observable.of(
        new CourseCreateResponse(false, 'error.invalidNumSpaces', null)
      );
    } else if (StubCourseService.createInvalidMiscErrorNumSpaces === course.numSpaces) {
      return Observable.of(
        new CourseCreateResponse(false, 'error.genericValidationError', null)
      );
    } else {
      return Observable.throw('API error');
    }
  }

}
