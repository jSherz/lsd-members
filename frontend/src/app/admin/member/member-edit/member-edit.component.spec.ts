/* tslint:disable:no-unused-variable */

import { ActivatedRoute, Router } from "@angular/router";
import { TestBed, async, inject } from "@angular/core/testing";
import { MemberEditComponent } from "./member-edit.component";

import { TestModule } from "../../../test.module";

describe("Component: MemberEdit", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [
        { provide: Router, useValue: { navigate: () => {} } },
        { provide: ActivatedRoute, useValue: { params: [{ uuid: "12345" }] } },
        MemberEditComponent
      ]
    });
  });

  it("should create an instance", async(
    inject([MemberEditComponent], (component: MemberEditComponent) => {
      expect(component).toBeTruthy();
    })
  ));
});
