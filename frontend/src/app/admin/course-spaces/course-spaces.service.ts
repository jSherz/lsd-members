import { Injectable                              } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable                              } from 'rxjs';
import { ErrorObservable                         } from 'rxjs/observable/ErrorObservable';


/**
 * A service for manipulating the spaces on a course.
 */
export abstract class CourseSpaceService {

  /**
   * Add the specified member to a space on a course.
   *
   * Will error if there's already a member in that space or if the member is already on this course in another space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  abstract addMember(uuid: string, memberUuid: string): Observable<string>

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  abstract removeMember(uuid: string, memberUuid: string): Observable<string>

}

@Injectable()
export class CourseSpaceServiceImpl extends CourseSpaceService {

  private addMemberUrl = 'http://localhost:8080/api/v1/course-spaces/add-member';
  private removeMemberUrl = 'http://localhost:8080/api/v1/course-spaces/remove-member';

  constructor(private http: Http) {
    super();
  }

  /**
   * Add the specified member to a space on a course.
   *
   * Will error if there's already a member in that space or if the member is already on this course in another space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  addMember(uuid: string, memberUuid: string): Observable<string> {
    let request = {
      uuid: uuid,
      memberUuid: memberUuid
    };

    return this.postAsJson(this.addMemberUrl, request);
  }

  /**
   * Remove a member from a space on a course.
   *
   * Will error if the member isn't in this space.
   *
   * @param uuid Course space UUID
   * @param memberUuid
   */
  removeMember(uuid: string, memberUuid: string): Observable<string> {
    let request = {
      uuid: uuid,
      memberUuid: memberUuid
    };

    return this.postAsJson(this.removeMemberUrl, request);
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  private extractJson(res: Response): string {
    return res.json() || null;
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   *
   * @param err
   * @param caught
   * @returns {ErrorObservable}
   */
  private handleError<R, T>(err: any, caught: Observable<T>): ErrorObservable {
    let errMsg = (err.message) ? err.message : err.status ? `${err.status} - ${err.statusText}` : 'Server error';
    console.error(errMsg);

    return Observable.throw(new Error(errMsg));
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

    return this.http.post(url, body, options)
      .map(this.extractJson)
      .catch(this.handleError);
  }

}
