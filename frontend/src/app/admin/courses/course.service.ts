import {Injectable} from '@angular/core';
import * as moment    from 'moment';
import {RequestOptions, Headers, Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";

export class Course {
  constructor (uuid: String, date: moment.Moment, organiserUuid: String, secondaryOrganiserUuid: String) {}
}

export class CourseWithNumSpaces {
  constructor (course: Course, totalSpace: number, openSpaces: number) {}
}

/**
 * Describes a service capable of retrieving course information.
 */
export abstract class CourseService {

  abstract find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]>

}

/**
 * A CourseService backed by the REST API.
 */
@Injectable()
export class CourseServiceImpl extends CourseService {

  private coursesFindUrl = 'http://localhost:8080/admin/courses'

  constructor(private http: Http) {
    super();
  }

  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    let body = JSON.stringify({
      startDate: startDate,
      endDate: endDate
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this.coursesFindUrl, body, options)
      .map(this.extractData)
      .catch(this.handleError)
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns {ArrayBuffer|string|Data|Uint8ClampedArray|any|Observable<Data>}
   */
  private extractData(res: Response) {
    let body = res.json();
    return body.data || {};
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

}
