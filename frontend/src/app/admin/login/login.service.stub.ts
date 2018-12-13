import { throwError as observableThrowError, Observable, of } from "rxjs";
import { Injectable } from "@angular/core";

import { LoginResult } from "./login-result";
import { LoginService } from "./login.service";

@Injectable()
export class LoginServiceStub extends LoginService {
  static validEmail = "AbbyNorth@teleworm.us";
  static validPassword = "Password1";

  static invalidEmail = "MohammedCharlton@jourrapide.com";
  static invalidPassword = "Hunter2";

  static accountLockedEmail = "MohammedCharlton@jourrapide.com";
  static accountLockedPassword = "qwertyuiop";

  static apiFailEmail = "AmyFarrell@armyspy.com";
  static apiFailPassword = "P455w0rd";

  constructor() {
    super(null, null);
  }

  login(email: string, password: string): Observable<LoginResult> {
    if (
      email === LoginServiceStub.validEmail &&
      password === LoginServiceStub.validPassword
    ) {
      return of(
        new LoginResult(true, {}, "228b0dea-39ec-45c6-b8a4-f45cda24fac2")
      );
    } else if (
      email === LoginServiceStub.invalidEmail &&
      password === LoginServiceStub.invalidPassword
    ) {
      return of(
        new LoginResult(
          false,
          {
            password: "error.invalidUserPass"
          },
          null
        )
      );
    } else if (
      email === LoginServiceStub.accountLockedEmail &&
      password === LoginServiceStub.accountLockedPassword
    ) {
      return of(
        new LoginResult(
          false,
          {
            email: "error.accountLocked"
          },
          null
        )
      );
    } else if (
      email === LoginServiceStub.apiFailEmail &&
      password === LoginServiceStub.apiFailPassword
    ) {
      return observableThrowError("Internal server error");
    } else {
      return observableThrowError("Unknown details used with stub");
    }
  }
}
