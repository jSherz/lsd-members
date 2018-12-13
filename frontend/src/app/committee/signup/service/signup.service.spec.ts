/* tslint:disable:no-unused-variable */

import { inject, TestBed, async } from "@angular/core/testing";
import { Http } from "@angular/http";

import { SignupService, SignupServiceImpl } from "./signup.service";

import { APP_VERSION } from "app/app.module";
import { JwtService } from "app/members/login/jwt.service";
import { StubJwtService } from "app/members/login/jwt.service.stub";

describe("Service: Signup", () => {
  const dummyHttp = {
    post() {
      return null;
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: JwtService,
          useValue: new StubJwtService("341234.12412312.1213", true)
        },
        { provide: APP_VERSION, useValue: "version.8888" },
        { provide: Http, useValue: dummyHttp },
        { provide: SignupService, useClass: SignupServiceImpl }
      ]
    });
  });

  it("should ...", async(
    inject([SignupService], (service: SignupService) => {
      expect(service).toBeTruthy();
    })
  ));
});
