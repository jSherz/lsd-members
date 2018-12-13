import {
  async,
  ComponentFixture,
  inject,
  TestBed
} from "@angular/core/testing";
import { ActivatedRoute, Params, Router } from "@angular/router";

import { PerformLoginComponent } from "./perform-login.component";
import { SocialLoginServiceStub } from "../social-login.service.stub";
import { SocialLoginService } from "../social-login.service";
import { JwtService } from "../../login/jwt.service";
import { StubJwtService } from "app/members/login/jwt.service.stub";
import Spy = jasmine.Spy;
import { RouterTestingModule } from "@angular/router/testing";

describe("PerformLoginComponent", () => {
  let component: PerformLoginComponent;
  let fixture: ComponentFixture<PerformLoginComponent>;

  let service: SocialLoginServiceStub;
  let jwtService: JwtService;
  let jwtSpy: Spy;

  const serviceFactory = () => {
    service = new SocialLoginServiceStub();
    return service;
  };

  const jwtServiceFactory = () => {
    jwtService = new StubJwtService("12345", false);
    jwtSpy = spyOn(jwtService, "setJwt");
    return jwtService;
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: "members", component: PerformLoginComponent }
        ])
      ],
      declarations: [PerformLoginComponent],
      providers: [
        { provide: SocialLoginService, useFactory: serviceFactory },
        { provide: JwtService, useFactory: jwtServiceFactory }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PerformLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it("should redirect the user to the login page when no query parameters are specified", async(
    inject(
      [ActivatedRoute, Router],
      (activatedRoute: ActivatedRoute, router: Router) => {
        expect(component).toBeTruthy();

        const navSpy = spyOn(router, "navigate");

        (<any>activatedRoute.queryParams).next({});

        fixture.whenStable().then(() => {
          expect(navSpy).toHaveBeenCalledWith(["members"]);
          expect(component.loginFailed).toEqual(false);
        });
      }
    )
  ));

  it("should show an error when logging in with the specified verification code fails", async(
    inject(
      [ActivatedRoute, Router],
      (activatedRoute: ActivatedRoute, router: Router) => {
        const navSpy = spyOn(router, "navigate");

        (<any>activatedRoute.queryParams).next({
          code: "FAIL_LOGIN"
        });

        fixture.whenStable().then(() => {
          expect(navSpy).toHaveBeenCalledTimes(0);
          expect(component.loginFailed).toEqual(true);
        });
      }
    )
  ));

  it("should show an error when making the verification call fails", async(
    inject(
      [ActivatedRoute, Router],
      (activatedRoute: ActivatedRoute, router: Router) => {
        const navSpy = spyOn(router, "navigate");

        (<any>activatedRoute.queryParams).next({
          code: "FAIL_LOGIN_REQUEST"
        });

        fixture.whenStable().then(() => {
          expect(navSpy).toHaveBeenCalledTimes(0);
          expect(component.loginFailed).toEqual(true);
        });
      }
    )
  ));

  it("should store the returned jwt and navigate the user to the dashboard when login succeeds", async(
    inject(
      [ActivatedRoute, Router],
      (activatedRoute: ActivatedRoute, router: Router) => {
        const navSpy = spyOn(router, "navigate");

        (<any>activatedRoute.queryParams).next({
          code: "SOME_RANDOM_CODE"
        });

        fixture.whenStable().then(() => {
          expect(navSpy).toHaveBeenCalledWith(["members", "dashboard"]);
          expect(jwtSpy).toHaveBeenCalledWith("jwt.1.2", false);
          expect(component.loginFailed).toEqual(false);
        });
      }
    )
  ));

  it("should navigate the user to the committee dashboard if they're a committee member", async(
    inject(
      [ActivatedRoute, Router],
      (activatedRoute: ActivatedRoute, router: Router) => {
        const navSpy = spyOn(router, "navigate");

        (<any>activatedRoute.queryParams).next({
          code: "COMMITTEE"
        });

        fixture.whenStable().then(() => {
          expect(navSpy).toHaveBeenCalledWith([
            "members",
            "committee",
            "dashboard"
          ]);
          expect(jwtSpy).toHaveBeenCalledWith("jwt.1.2", true);
          expect(component.loginFailed).toEqual(false);
        });
      }
    )
  ));
});
