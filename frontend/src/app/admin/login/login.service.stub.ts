import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {LoginResult} from './login-result';
import {LoginService} from './login.service';


@Injectable()
export class LoginServiceStub extends LoginService {

  static validEmail: string = 'AbbyNorth@teleworm.us';
  static validPassword: string = 'Password1';

  static invalidEmail: string = 'MohammedCharlton@jourrapide.com';
  static invalidPassword: string = 'Hunter2';

  static accountLockedEmail: string = 'MohammedCharlton@jourrapide.com';
  static accountLockedPassword: string = 'qwertyuiop';

  static apiFailEmail: string = 'AmyFarrell@armyspy.com';
  static apiFailPassword: string = 'P455w0rd';

  constructor() {
    super(null, null);
  }

  login(email: string, password: string): Observable<LoginResult> {
    if (email === LoginServiceStub.validEmail && password === LoginServiceStub.validPassword) {
      return Observable.of(new LoginResult(true, {}, '228b0dea-39ec-45c6-b8a4-f45cda24fac2'));
    } else if (email === LoginServiceStub.invalidEmail && password === LoginServiceStub.invalidPassword) {
      return Observable.of(new LoginResult(false, {
        'password': 'error.invalidUserPass'
      }, null));
    } else if (email === LoginServiceStub.accountLockedEmail && password === LoginServiceStub.accountLockedPassword) {
      return Observable.of(new LoginResult(false, {
        'email': 'error.accountLocked'
      }, null));
    } else if (email === LoginServiceStub.apiFailEmail && password === LoginServiceStub.apiFailPassword) {
      return Observable.throw('Internal server error');
    } else {
      return Observable.throw('Unknown details used with stub');
    }
  }

}
