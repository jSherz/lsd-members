import { TestBed, inject } from "@angular/core/testing";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { Observable, of } from "rxjs";

import { DashboardService, DashboardServiceImpl } from "./dashboard.service";
import { APP_VERSION } from "../../app.module";
import { StubJwtService } from "../login/jwt.service.stub";
import { JwtService } from "../login/jwt.service";
import { BasicInfo } from "./basic-info";

const dummyHttp = {
  get() {
    return of(
      new BasicInfo(
        "ff37543a-ba29-4f97-afef-efa9a14e3463",
        "Bobby",
        "Smith",
        "2017-07-10 20:29:00"
      )
    );
  }
};

describe("DashboardService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: JwtService,
          useValue: new StubJwtService("341234.12412312.1213", true)
        },
        { provide: APP_VERSION, useValue: "version.8888" },
        { provide: HttpClient, useValue: dummyHttp },
        { provide: DashboardService, useClass: DashboardServiceImpl }
      ]
    });
  });

  it("returns the basic info", inject(
    [DashboardService],
    (service: DashboardService) => {
      service.getBasicInfo().subscribe(basicInfo => {
        expect(basicInfo.uuid).toEqual("ff37543a-ba29-4f97-afef-efa9a14e3463");
        expect(basicInfo.firstName).toEqual("Bobby");
        expect(basicInfo.lastName).toEqual("Smith");
        expect(basicInfo.createdAt).toEqual("2017-07-10 20:29:00");
      });
    }
  ));

  it("returns null when the user is not authenticated", inject(
    [HttpClient],
    (http: HttpClient) => {
      const jwtService = new StubJwtService(null, false);
      const service = new DashboardServiceImpl(
        http,
        jwtService,
        "sdookkan.asfef.agfgdf"
      );

      service.getBasicInfo().subscribe(basicInfo => {
        expect(basicInfo).toBeNull();
      });
    }
  ));
});
