import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

import { ApiKeyService, BaseService } from "../utils";
import {
  CourseSpaceDepositPaidResponse,
  CourseSpaceMemberResponse
} from "./model";
import { environment } from "../../../environments/environment";
import { catchError } from "rxjs/operators";

/**
 * A service for manipulating the spaces on a course.
 */
export abstract class CourseSpaceService extends BaseService {
  constructor(http: HttpClient, apiKeyService: ApiKeyService) {
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
  abstract addMember(
    uuid: string,
    memberUuid: string
  ): Observable<CourseSpaceMemberResponse>;

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  abstract removeMember(
    uuid: string,
    memberUuid: string
  ): Observable<CourseSpaceMemberResponse>;

  /**
   * Set a space to have the deposit paid or not paid.
   *
   * @param uuid
   * @param depositPaid
   */
  abstract setDepositPaid(
    uuid: string,
    depositPaid: boolean
  ): Observable<CourseSpaceDepositPaidResponse>;
}

@Injectable()
export class CourseSpaceServiceImpl extends CourseSpaceService {
  private baseUrl = environment.apiUrl + "/api/v1/course-spaces/";
  private addMemberUrl = this.baseUrl + "{{uuid}}/add-member";
  private removeMemberUrl = this.baseUrl + "{{uuid}}/remove-member";
  private setDepositPaidUrl = this.baseUrl + "{{uuid}}/deposit-paid";

  constructor(http: HttpClient, apiKeyService: ApiKeyService) {
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
  addMember(
    uuid: string,
    memberUuid: string
  ): Observable<CourseSpaceMemberResponse> {
    const request = { memberUuid: memberUuid };

    return this.post<CourseSpaceMemberResponse>(
      this.addMemberUrl.replace("{{uuid}}", uuid),
      request
    ).pipe(catchError(this.handleError()));
  }

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  removeMember(
    uuid: string,
    memberUuid: string
  ): Observable<CourseSpaceMemberResponse> {
    const request = { memberUuid: memberUuid };

    return this.post<CourseSpaceMemberResponse>(
      this.removeMemberUrl.replace("{{uuid}}", uuid),
      request
    ).pipe(catchError(this.handleError()));
  }

  /**
   * Set a space to have the deposit paid or not paid.
   *
   * @param uuid
   * @param depositPaid
   */
  setDepositPaid(
    uuid: string,
    depositPaid: boolean
  ): Observable<CourseSpaceDepositPaidResponse> {
    const request = { depositPaid: depositPaid };

    return this.put<CourseSpaceDepositPaidResponse>(
      this.setDepositPaidUrl.replace("{{uuid}}", uuid),
      request
    ).pipe(catchError(this.handleError()));
  }
}
