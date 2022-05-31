import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { DashboardStatsComponent } from "./dashboard-stats.component";
import { DashboardStatsService } from "./dashboard-stats.service";
import { StubJwtService } from "../../../members/login/jwt.service.stub";
import { JwtService } from "../../../members/login/jwt.service";
import { HttpClientModule } from "@angular/common/http";
import { StubDashboardStatsService } from "./stub-committee-stats.service";
import { RouterTestingModule } from "@angular/router/testing";

describe("DashboardStatsComponent", () => {
  let component: DashboardStatsComponent;
  let fixture: ComponentFixture<DashboardStatsComponent>;
  let service: StubDashboardStatsService;

  const serviceFactory = () => {
    service = new StubDashboardStatsService();
    return service;
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
      declarations: [DashboardStatsComponent],
      providers: [
        { provide: DashboardStatsService, useFactory: serviceFactory },
        { provide: JwtService, useValue: new StubJwtService("asdf", true) }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should show the returned number of received messages", () => {
    service.numReceived.next(13781);

    expect(component.numReceived).toEqual(13781);
  });
});
