import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

import { Observable } from "rxjs";

import { environment } from "../../../environments/environment";
import { SocialLoginRequest, SocialLoginResponse } from "./model";
import { map } from "rxjs/operators";

export abstract class SocialLoginService {
  abstract login(verificationCode: string): Observable<SocialLoginResponse>;
}

@Injectable()
export class SocialLoginServiceImpl implements SocialLoginService {
  private http: HttpClient;

  private baseEndpoint: string = environment.apiUrl + "/api/v1/social-login";
  private loginEndpoint: string = this.baseEndpoint + "/verify";

  constructor(http: HttpClient) {
    this.http = http;
  }

  login(verificationCode: string): Observable<SocialLoginResponse> {
    const request = new SocialLoginRequest(verificationCode);

    return this.http
      .post(this.loginEndpoint, request)
      .pipe(map(r => r as SocialLoginResponse));
  }
}
