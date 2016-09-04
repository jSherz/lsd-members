import { Injectable } from '@angular/core';
import { Http       } from '@angular/http';
import { Observable } from 'rxjs';

import { ApiKeyService, BaseService } from '../utils';


export class CourseSpaceMemberResponse {
  success: boolean;
  error: string;
}

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

}

@Injectable()
export class CourseSpaceServiceImpl extends CourseSpaceService {

  private addMemberUrl = 'http://localhost:8080/api/v1/course-spaces/{{uuid}}/add-member';
  private removeMemberUrl = 'http://localhost:8080/api/v1/course-spaces/{{uuid}}/remove-member';

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
    let request = { memberUuid: memberUuid };

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
    let request = { memberUuid: memberUuid };

    return this.post(this.removeMemberUrl.replace('{{uuid}}', uuid), request)
      .map(r => this.extractJson<CourseSpaceMemberResponse>(r))
      .catch(this.handleError());
  }

}
