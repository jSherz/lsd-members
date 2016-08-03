import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import '../rxjs-operators';

export class SignupResult {

  constructor(
    success: boolean,
    errors: [{ [field: string]: string; }]
  ) {}

}

export interface ISignupService {

  signup(name: string, email?: string, phoneNumber?: string): Observable<SignupResult>;

}

@Injectable()
export class SignupService implements ISignupService {

  private signupUrl: string = 'signup';

  constructor(private http: Http) {}

  signup(name: string, email?: string, phoneNumber?: string): Observable<SignupResult> {
    let body = JSON.stringify({
      name: name,
      email: email,
      phoneNumber: phoneNumber
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(this.signupUrl, body, options)
                    .map(this.extractData)
                    .catch(this.handleError);
  }

  private extractData(res: Response) {
    return res.json().data || {};
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
