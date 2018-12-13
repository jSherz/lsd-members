/* tslint:disable:no-unused-variable */

import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";
import {
  async,
  ComponentFixture,
  inject,
  TestBed,
  fakeAsync,
  tick
} from "@angular/core/testing";
import { Http, Response, ResponseOptions } from "@angular/http";
import { Observable, of } from "rxjs";

import { JwtLoginService, JwtLoginServiceImpl } from "./jwt-login.service";
import { LoginResult } from "./login-result";
import { StubJwtService } from "./jwt.service.stub";

describe("JwtLoginService", () => {
  const appVersion = "jwt-login-service-spec";

  const dummyHttp = {
    post: (url, request) => {
      console.log("JwtLoginService requested " + url);

      if (request === '{"signedRequest":"itzalive"}') {
        const body = JSON.stringify(
          new LoginResult(true, null, "my.little.jwt", false)
        );

        return of(new Response(new ResponseOptions({ body, status: 200 })));
      } else {
        const body = JSON.stringify(
          new LoginResult(false, "wrong login something", null, false)
        );

        return of(new Response(new ResponseOptions({ body, status: 401 })));
      }
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [],
      imports: [],
      providers: [{ provide: Http, useValue: dummyHttp }]
    }).compileComponents();
  });

  it("stores the returned token in the JwtService", async(
    inject([Http], (http: Http) => {
      const jwtService = new StubJwtService("original-jwt-value", false);
      const service = new JwtLoginServiceImpl(http, jwtService, appVersion);

      expect(jwtService.getJwt()).toEqual("original-jwt-value");

      service.login("itzalive").subscribe((loginResult: LoginResult) => {
        expect(loginResult.success).toEqual(true);
        expect(loginResult.error).toEqual(null);
        expect(loginResult.jwt).toEqual("my.little.jwt");
      });

      expect(jwtService.getJwt()).toEqual("my.little.jwt");
    })
  ));

  it("clears the JWT if the login fails", async(
    inject([Http], (http: Http) => {
      const jwtService = new StubJwtService("original-jwt-value", false);
      const service = new JwtLoginServiceImpl(http, jwtService, appVersion);

      expect(jwtService.getJwt()).toEqual("original-jwt-value");

      service
        .login("this-will-return-an-error")
        .subscribe((loginResult: LoginResult) => {
          expect(loginResult.success).toEqual(false);
        });

      expect(jwtService.getJwt()).toEqual("");
    })
  ));
});
