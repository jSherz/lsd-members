/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from "@angular/core/testing";

import { MemberLookupComponent } from "./member-lookup.component";
import { TestModule } from "../../../../test.module";

describe("Component: MemberLookup", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TestModule],
      providers: [MemberLookupComponent]
    });
  });

  it("should create an instance", async(
    inject([MemberLookupComponent], (component: MemberLookupComponent) => {
      expect(component).toBeTruthy();
    })
  ));
});
