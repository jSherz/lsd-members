/* eslint-disable @typescript-eslint/no-unused-vars */

import { ActivatedRoute, Router } from "@angular/router";
import { TestBed, inject, waitForAsync } from "@angular/core/testing";
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

  it(
    "should create an instance",
    waitForAsync(
      inject([MemberEditComponent], (component: MemberEditComponent) => {
        expect(component).toBeTruthy();
      })
    )
  );
});
