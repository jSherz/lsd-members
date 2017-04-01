import {Subject, Observable} from 'rxjs';
import * as moment from 'moment';

import {CourseService} from './course.service';
import {
  CourseWithOrganisers,
  CourseSpaceWithMember,
  CourseCreateResponse,
  CourseWithNumSpaces,
  CourseCreateRequest,
  Course,
  CommitteeMember
} from './model';
import {stubExampleSpaces} from './model/stub-example-spaces';


/**
 * A fake course service, used for testing.
 */
export class StubCourseService extends CourseService {

  static getDontCompleteUuid = '692d2968-5600-4e88-9111-1c439e002edf';
  static getApiErrorUuid = '5cb2b7b7-8edb-4e0c-aaa0-a46bf0bc6a81';
  static getOkUuid = '04cf71d9-d709-4f09-af2c-2390cec2b728';
  static getOkCourse = new CourseWithOrganisers(
    new Course('04cf71d9-d709-4f09-af2c-2390cec2b728', moment('2014-12-18'), '91a35643-3fd0-403b-b90f-c612092cc97b', null, 1),
    new CommitteeMember('91a35643-3fd0-403b-b90f-c612092cc97b', 'Taylor Moore'),
    null
  );

  static spacesDontCompleteUuid = StubCourseService.getDontCompleteUuid;
  static spacesApiErrorUuid = '7f03b5bb-ee2c-4879-a3aa-02be3721015f';
  static spacesOkUuid = '04cf71d9-d709-4f09-af2c-2390cec2b728';

  static createDontCompleteSubject = new Subject<CourseCreateResponse>();
  static createDontCompleteNumSpaces = 14;
  static createApiErrorNumSpaces = 11;
  static createOkNumSpaces = 5;
  static createInvalidNumSpaces = 51;
  static createInvalidMiscErrorNumSpaces = 13;

  getDontCompleteSubject = new Subject<CourseWithOrganisers>();
  spacesDontCompleteSubject = new Subject<CourseSpaceWithMember[]>();

  getByUuid(uuid: string): Observable<CourseWithOrganisers> {
    //  || StubCourseService.spacesApiErrorUuid === uuid
    if (StubCourseService.getDontCompleteUuid === uuid) {
      return this.getDontCompleteSubject;
    } else if (StubCourseService.getApiErrorUuid === uuid) {
      return Observable.throw('API error');
    } else if (StubCourseService.getOkUuid === uuid || StubCourseService.spacesApiErrorUuid === uuid) {
      // Pass through to this if the spaces method will error - allows testing error handling for get then spaces
      return Observable.of(StubCourseService.getOkCourse);
    } else {
      return Observable.throw('Unknown uuid used with stub');
    }
  }

  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    if (StubCourseService.spacesDontCompleteUuid === uuid) {
      return this.spacesDontCompleteSubject;
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
