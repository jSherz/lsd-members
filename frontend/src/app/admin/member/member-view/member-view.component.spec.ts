/* tslint:disable:no-unused-variable */

import { ActivatedRoute } from "@angular/router";
import { TestBed, async, inject } from "@angular/core/testing";

import { MemberViewComponent } from "./member-view.component";
import { TestModule } from "../../../test.module";
import { MemberService } from "../member.service";
import { StubMemberService } from "../member.service.stub";

describe("Component: MemberView", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRoute() },
        { provide: MemberService, useValue: new StubMemberService() },
        MemberViewComponent
      ]
    });
  });

  it("should create an instance", async(
    inject([MemberViewComponent], (component: MemberViewComponent) => {
      expect(component).toBeTruthy();
    })
  ));
});
