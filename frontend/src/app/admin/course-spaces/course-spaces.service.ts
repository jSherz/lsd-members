import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {ApiKeyService, BaseService} from '../utils';
import {CourseSpaceDepositPaidResponse, CourseSpaceMemberResponse} from './model';
import {environment} from '../../../environments/environment';


/**
 * A service for manipulating the spaces on a course.
 */
export abstract class CourseSpaceService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  /**
   * Add the specified member to a space on a course.
   *
   * Will error if there's already a member in that space or if the member is already on this course in another space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  abstract addMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse>

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  abstract removeMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse>

  /**
   * Set a space to have the deposit paid or not paid.
   *
   * @param uuid
   * @param depositPaid
   */
  abstract setDepositPaid(uuid: string, depositPaid: boolean): Observable<CourseSpaceDepositPaidResponse>

}

@Injectable()
export class CourseSpaceServiceImpl extends CourseSpaceService {

  private baseUrl = environment.apiUrl + '/api/v1/course-spaces/';
  private addMemberUrl = this.baseUrl + '{{uuid}}/add-member';
  private removeMemberUrl = this.baseUrl + '{{uuid}}/remove-member';
  private setDepositPaidUrl = this.baseUrl + '{{uuid}}/deposit-paid';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  /**
   * Add the specified member to a space on a course.
   *
   * Will error if there's already a member in that space or if the member is already on this course in another space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  addMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    const request = {memberUuid: memberUuid};

    return this.post(this.addMemberUrl.replace('{{uuid}}', uuid), request)
      .map(r => this.extractJson<CourseSpaceMemberResponse>(r))
      .catch(this.handleError());
  }

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  removeMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    const request = {memberUuid: memberUuid};

    return this.post(this.removeMemberUrl.replace('{{uuid}}', uuid), request)
      .map(r => this.extractJson<CourseSpaceMemberResponse>(r))
      .catch(this.handleError());
  }

  /**
   * Set a space to have the deposit paid or not paid.
   *
   * @param uuid
   * @param depositPaid
   */
  setDepositPaid(uuid: string, depositPaid: boolean): Observable<CourseSpaceDepositPaidResponse> {
    const request = {depositPaid: depositPaid};

    return this.put(this.setDepositPaidUrl.replace('{{uuid}}', uuid), request)
      .map(r => this.extractJson<CourseSpaceDepositPaidResponse>(r))
      .catch(this.handleError());
  }

}
