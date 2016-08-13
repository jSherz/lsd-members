import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';

export class SignupResult {

  success: boolean;
  errors: any;

  constructor(success: boolean, errors: any) {
    this.success = success;
    this.errors = errors;
  }

}

export abstract class SignupService {

  abstract signup(name: string, phoneNumber?: string): Observable<SignupResult>;

  abstract signupAlt(name: string, email?: string): Observable<SignupResult>;

}

@Injectable()
export class SignupServiceImpl extends SignupService {

  private signupUrl: string = 'http://localhost:8080/api/v1/members/sign-up';
  private signupAltUrl: string = 'http://localhost:8080/api/v1/members/sign-up/alt';

  constructor(private http: Http) {
    super();
  }

  signup(name: string, phoneNumber: string): Observable<SignupResult> {
    let request = {
      name: name,
      phoneNumber: phoneNumber
    };

    return this.doSignup(this.signupUrl, request);
  }

  signupAlt(name: string, email?: string): Observable<SignupResult> {
    let request = {
      name: name,
      email: email
    };

    return this.doSignup(this.signupAltUrl, request);
  }

  private doSignup(url: string, request: any): Observable<SignupResult> {
    let body = JSON.stringify(request);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(url, body, options)
      .map(res => res.json() || {})
      .catch(this.handleError);
  }

  private handleError(error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }

}
