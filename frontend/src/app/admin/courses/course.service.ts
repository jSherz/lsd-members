import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import * as moment from 'moment';

import {BaseService} from '../utils/base.service';
import {ApiKeyService} from '../utils/api-key.service';
import {CourseCreateRequest, CourseCreateResponse} from './model';

export class Course {
  uuid: String;
  date: moment.Moment;
  organiserUuid: String;
  secondaryOrganiserUuid: String;
  status: number;

  constructor(uuid: String, date: moment.Moment, organiserUuid: String, secondaryOrganiserUuid: String, status: number) {
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

  constructor(course: Course, totalSpaces: number, spacesFree: number) {
    this.course = course;
    this.totalSpaces = totalSpaces;
    this.spacesFree = spacesFree;
  }
}

export class CommitteeMember {
  name: string;
  uuid: string;

  constructor(name: string, uuid: string) {
    this.name = name;
    this.uuid = uuid;
  }
}

export class CourseWithOrganisers {
  course: Course;
  organiser: CommitteeMember;
  secondaryOrganiser: CommitteeMember;

  constructor(course: Course, organiser: CommitteeMember, secondaryOrganiser: CommitteeMember) {
    this.course = course;
    this.organiser = organiser;
    this.secondaryOrganiser = secondaryOrganiser;
  }
}

export class StrippedMember {
  firstName: string;
  lastName: string;
  uuid: string;
  weight: number;
  height: number;
  createdAt: moment.Moment;

  constructor(firstName: string, lastName: string, uuid: string, weight: number, height: number, createdAt: moment.Moment) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.uuid = uuid;
    this.weight = weight;
    this.height = height;
    this.createdAt = createdAt;
  }

  infoComplete() {
    return this.firstName != null && this.lastName != null && this.weight != null && this.height != null;
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
    this.member = member;
  }
}

/**
 * Describes a service capable of retrieving course information.
 */
export abstract class CourseService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]>

  abstract getByUuid(uuid: string): Observable<CourseWithOrganisers>

  abstract spaces(uuid: string): Observable<CourseSpaceWithMember[]>

  abstract create(course: CourseCreateRequest): Observable<CourseCreateResponse>

}

/**
 * A CourseService backed by the REST API.
 */
@Injectable()
export class CourseServiceImpl extends CourseService {

  private baseUrl = 'http://localhost:8080/api/v1/courses';
  private coursesFindUrl = this.baseUrl;
  private coursesGetUrl = this.baseUrl + '/{{uuid}}';
  private courseSpacesUrl = this.baseUrl + '/{{uuid}}/spaces';
  private coursesCreateUrl = this.baseUrl + '/create';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
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

    return this.post(this.coursesFindUrl, body)
      .map(r => this.extractJson<CourseWithNumSpaces[]>(r))
      .catch(this.handleError());
  }

  /**
   * Get the course with the given UUID.
   *
   * @param uuid
   * @returns {undefined}
   */
  getByUuid(uuid: string): Observable<CourseWithOrganisers> {
    return this.get(this.coursesGetUrl.replace('{{uuid}}', uuid))
      .map(r => this.extractJson<CourseWithOrganisers>(r))
      .catch(this.handleError());
  }

  /**
   * Get the spaces on the given course.
   *
   * @param uuid
   * @returns {undefined}
   */
  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    return this.get(this.courseSpacesUrl.replace('{{uuid}}', uuid))
      .map(r => this.extractJson<CourseSpaceWithMember[]>(r))
      .catch(this.handleError());
  }

  /**
   * Create a new course.
   *
   * @param course
   * @returns {undefined} course UUID
   */
  create(course: CourseCreateRequest): Observable<CourseCreateResponse> {
    return this.post(this.coursesCreateUrl, course)
      .map(r => this.extractJson<CourseCreateResponse>(r))
      .catch(this.handleError());
  }

}
