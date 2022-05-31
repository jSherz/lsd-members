/* eslint-disable @typescript-eslint/no-unused-vars */

import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";
import { async, inject } from "@angular/core/testing";
import { CommitteeComponent } from "./committee.component";

describe("Component: Committee", () => {
  it("should create an instance", () => {
    const component = new CommitteeComponent();
    expect(component).toBeTruthy();
  });
});
