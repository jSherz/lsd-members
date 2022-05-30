import { HttpClient, HttpResponse } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

import { BaseService } from "../utils/base.service";
import { LoginResult } from "./login-result";
import { environment } from "../../../environments/environment";
import { JwtService } from "./jwt.service";
import { APP_VERSION } from "../../app.module";
import { map } from "rxjs/operators";

export abstract class JwtLoginService extends BaseService {
  constructor(http: HttpClient, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract login(signedFbRequest: String): Observable<LoginResult>;
}

@Injectable()
export class JwtLoginServiceImpl extends JwtLoginService {
  loginUrl = environment.apiUrl + "/api/v1/social-login";

  constructor(
    http: HttpClient,
    jwtService: JwtService,
    @Inject(APP_VERSION) appVersion: string
  ) {
    super(http, jwtService, appVersion);
  }

  login(signedRequest: String): Observable<LoginResult> {
    return this.post<HttpResponse<LoginResult>>(this.loginUrl, {
      signedRequest
    }).pipe(
      map((response: HttpResponse<LoginResult>) => {
        const result = response.body;

        if (result.success) {
          this.jwtService.setJwt(result.jwt, result.committeeMember);
        } else {
          this.jwtService.setJwt("", false);
        }

        return result;
      })
    );
  }
}
