import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs';

import * as moment from 'moment';

import {BaseService} from '../utils/base.service';
import {ApiKeyService} from '../utils/api-key.service';
import {
  CourseCreateRequest,
  CourseCreateResponse,
  CourseWithNumSpaces,
  CourseWithOrganisers,
  CourseSpaceWithMember
} from './model';
import {environment} from '../../../environments/environment';
import { catchError, map } from 'rxjs/operators';


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

  private baseUrl = environment.apiUrl + '/api/v1/courses';
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
    const body = {
      startDate: startDate.format('YYYY-MM-DD'),
      endDate: endDate.format('YYYY-MM-DD')
    };

    return this.post(this.coursesFindUrl, body)
      .pipe(
        map(r => this.extractJson<CourseWithNumSpaces[]>(r)),
        catchError(this.handleError())
      );
  }

  /**
   * Get the course with the given UUID.
   *
   * @param uuid
   * @returns {undefined}
   */
  getByUuid(uuid: string): Observable<CourseWithOrganisers> {
    return this.get(this.coursesGetUrl.replace('{{uuid}}', uuid))
      .pipe(
        map(r => this.extractJson<CourseWithOrganisers>(r)),
        catchError(this.handleError())
      );
  }

  /**
   * Get the spaces on the given course.
   *
   * @param uuid
   * @returns {undefined}
   */
  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    return this.get(this.courseSpacesUrl.replace('{{uuid}}', uuid))
      .pipe(
        map(r => this.extractJson<CourseSpaceWithMember[]>(r)),
        catchError(this.handleError())
      );
  }

  /**
   * Create a new course.
   *
   * @param course
   * @returns {undefined} course UUID
   */
  create(course: CourseCreateRequest): Observable<CourseCreateResponse> {
    return this.post(this.coursesCreateUrl, course)
      .pipe(
        map(r => this.extractJson<CourseCreateResponse>(r)),
        catchError(this.handleError())
      );
  }

}
