import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Response, Headers, RequestOptions, Http } from '@angular/http';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';

export class LoginResult {

  success: boolean;
  errors: any;
  apiKey: string;

  constructor(success: boolean, errors: any, apiKey: string) {
    this.success = success;
    this.errors = errors;
    this.apiKey = apiKey;
  }

}

export abstract class LoginService {

  abstract login(email: string, password: string): Observable<LoginResult>;

}

@Injectable()
export class LoginServiceImpl extends LoginService {

  loginUrl = "http://localhost:8080/api/v1/login";

  constructor(private http: Http) {
    super();
  }

  login(email: string, password: string): Observable<LoginResult> {
    let body = {
      email: email,
      password: password
    };

    return this.postAsJson(this.loginUrl, body)
      .map(this.extractJson)
      .catch(this.handleError);
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  private extractJson(res: Response): LoginResult[] {
    let body = res.json();
    return body || [];
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

    return this.http.post(url, body, options);
  }

}
