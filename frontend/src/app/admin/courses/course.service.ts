import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {RequestOptions, Headers, Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';

export class Course {
  uuid: String;
  date: moment.Moment;
  organiserUuid: String;
  secondaryOrganiserUuid: String;
  status: number;

  constructor (uuid: String, date: moment.Moment, organiserUuid: String, secondaryOrganiserUuid: String, status: number) {
    this.uuid = uuid;
    this.date = date;
    this.organiserUuid = organiserUuid;
    this.secondaryOrganiserUuid = secondaryOrganiserUuid;
    this.status = status;
  }
}

export class CourseWithNumSpaces {
  course: Course;
  totalSpaces: number;
  spacesFree: number;

  constructor (course: Course, totalSpaces: number, spacesFree: number) {
    this.course = course;
    this.totalSpaces = totalSpaces;
    this.spacesFree = spacesFree;
  }
}

export class CommitteeMember {
  name: string;
  uuid: string;

  constructor (name: string, uuid: string) {
    this.name = name;
    this.uuid = uuid;
  }
}

export class CourseWithOrganisers {
  course: Course;
  organiser: CommitteeMember;
  secondaryOrganiser: CommitteeMember;

  constructor (course: Course, organiser: CommitteeMember, secondaryOrganiser: CommitteeMember) {
    this.course = course;
    this.organiser = organiser;
    this.secondaryOrganiser = secondaryOrganiser;
  }
}

export class StrippedMember {
  name: string;
  uuid: string;
  createdAt: moment.Moment;

  constructor (name: string, uuid: string, createdAt: moment.Moment) {
    this.name = name;
    this.uuid = uuid;
    this.createdAt = createdAt;
  }
}

export class CourseSpaceWithMember {
  uuid: string;
  courseUuid: string;
  number: number;
  member: StrippedMember;

  constructor(uuid: string, courseUuid: string, number: number, member: StrippedMember) {
    this.uuid = uuid;
    this.courseUuid = courseUuid;
    this.number = number;
    this.member = StrippedMember;
  }
}

/**
 * Describes a service capable of retrieving course information.
 */
export abstract class CourseService {

  abstract find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]>

  abstract get(uuid: string): Observable<CourseWithOrganisers>

  abstract spaces(uuid: string): Observable<CourseSpaceWithMember[]>

}

/**
 * A CourseService backed by the REST API.
 */
@Injectable()
export class CourseServiceImpl extends CourseService {

  private coursesFindUrl = 'http://localhost:8080/api/v1/courses';
  private coursesGetUrl = 'http://localhost:8080/api/v1/courses/{{uuid}}';
  private courseSpacesUrl = 'http://localhost:8080/api/v1/courses/{{uuid}}/spaces'

  constructor(private http: Http) {
    super();
  }

  /**
   * Look for course(s) between the given dates (inclusive).
   *
   * @param startDate
   * @param endDate
   * @returns {Observable<R>}
   */
  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    let body = {
      startDate: startDate.format('YYYY-MM-DD'),
      endDate: endDate.format('YYYY-MM-DD')
    };

    return this.postAsJson(this.coursesFindUrl, body)
      .map(this.extractJson)
      .catch(this.handleError);
  }

  /**
   * Get the course with the given UUID.
   *
   * @param uuid
   * @returns {undefined}
   */
  get(uuid: string): Observable<CourseWithOrganisers> {
    return this.http.get(this.coursesGetUrl.replace("{{uuid}}", uuid))
      .map(this.extractJson)
      .catch(this.handleError);
  }

  /**
   * Get the spaces on the given course.
   *
   * @param uuid
   * @returns {undefined}
   */
  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    return this.http.get(this.courseSpacesUrl.replace("{{uuid}}", uuid))
      .map(this.extractJson)
      .catch(this.handleError);
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  private extractJson(res: Response): CourseWithNumSpaces[] {
    let body = res.json();
    return body || [];
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   *
   * @param error
   * @returns {ErrorObservable}
   */
  private handleError(error: any) {
    let errMsg = (error.message) ? error.message : error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg);

    return Observable.throw(errMsg);
  }

  /**
   * Build a post request to the given URL with the given data (serialized as JSON).
   *
   * The request is sent with a content type of 'application/json'.
   *
   * @param url
   * @param data
   * @returns {Observable<Response>}
   */
  private postAsJson(url: string, data: any) {
    let body = JSON.stringify(data);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(url, body, options);
  }

}
