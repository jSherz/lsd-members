import { Observable, of } from "rxjs";

import { JwtLoginService } from "./jwt-login.service";
import { LoginResult } from "./login-result";

export class JwtLoginServiceStub extends JwtLoginService {
  constructor() {
    super(null, null, null);
  }

  login(signedFbRequest: String): Observable<LoginResult> {
    return of(new LoginResult(true, null, "jwt.1.23", false));
  }
}
