import {Injectable} from '@angular/core';
import * as moment    from 'moment';
import {RequestOptions, Headers, Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";

export class Course {
  uuid: String;
  date: moment.Moment;
  organiserUuid: String;
  secondaryOrganiserUuid: String;

  constructor (uuid: String, date: moment.Moment, organiserUuid: String, secondaryOrganiserUuid: String) {
    this.uuid = uuid;
    this.date = date;
    this.organiserUuid = organiserUuid;
    this.secondaryOrganiserUuid = secondaryOrganiserUuid;
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

  private coursesFindUrl = 'http://localhost:8080/api/v1/courses'

  constructor(private http: Http) {
    super();
  }

  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    let body = JSON.stringify({
      startDate: startDate.format("YYYY-MM-DD"),
      endDate: endDate.format("YYYY-MM-DD")
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this.coursesFindUrl, body, options)
      .map(this.extractFindResult)
      .catch(this.handleError)
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  private extractFindResult(res: Response): CourseWithNumSpaces[] {
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

}
