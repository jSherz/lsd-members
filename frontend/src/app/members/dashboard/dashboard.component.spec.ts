import { TestBed, inject } from "@angular/core/testing";
import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse
} from "@angular/common/http";

import { DashboardService, DashboardServiceImpl } from "./dashboard.service";
import { APP_VERSION } from "../../app.module";
import { StubJwtService } from "../login/jwt.service.stub";
import { JwtService } from "../login/jwt.service";
import { DashboardComponent } from "./dashboard.component";
import { of, throwError } from "rxjs";

const failingHttp: any = {
  get() {
    return throwError(
      () =>
        new HttpErrorResponse({
          status: 500,
          // body: "DOES NOT COMPUTE! DOES NOT COMPUTE!",
          url: "https://aaaahhhhhhhh.example.com"
        })
    );
  }
};

describe("DashboardComponent", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: JwtService,
          useValue: new StubJwtService("341234.12412312.1213", true)
        },
        { provide: APP_VERSION, useValue: "version.8888" },
        { provide: HttpClient, useValue: failingHttp },
        { provide: DashboardService, useClass: DashboardServiceImpl }
      ]
    });
  });

  it("shows an error message when getting the dashboard information fails", inject(
    [JwtService, APP_VERSION],
    (jwtService: JwtService, appVersion: string) => {
      const service = new DashboardServiceImpl(
        failingHttp,
        jwtService,
        appVersion
      );

      const component = new DashboardComponent(service);
      component.ngOnInit();

      expect(component.dashboardLoadFailed).toEqual(true);
    }
  ));
});
