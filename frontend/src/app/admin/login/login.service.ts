import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

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

  constructor() {
    super();
  }

  login(email: string, password: string): Observable<LoginResult> {
    return Observable.of(
      new LoginResult(true, {}, '123123-123123-123-123')
    );
  }

}
