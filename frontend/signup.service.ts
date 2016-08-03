import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';

export interface ISignupService {
    signup(name: string, email?: string, phoneNumber?: string): Observable<SignupResult>;
}

export class SignupResult {
    constructor(success: boolean,
        errors: [{ [field: string]: string; }]) { }
}

@Injectable()
export class SignupService implements ISignupService {

    private signupUrl: string = 'signup';

    constructor(private http: Http) { }

    signup(name: string, email?: string, phoneNumber?: string): Observable<SignupResult> {
        let body = JSON.stringify({
            name: name,
            email: email,
            phoneNumber: phoneNumber
        });
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.http.post(this.signupUrl, body)
            .map(this.extractData)
            .catch(this.handleError);
    }

    private extractData(res: Response) {
        return res.json() || {};
    }

    private handleError(error: any) {
        let errMsg = (error.message) ? error.message :
            error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg); // log to console instead
        return Observable.throw(errMsg);
    }

}
