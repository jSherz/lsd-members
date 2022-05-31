/* eslint-disable @typescript-eslint/no-unused-vars */

import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";
import { async, inject } from "@angular/core/testing";
import { AboutComponent } from "./about.component";

describe("Component: About", () => {
  it("should create an instance", () => {
    const component = new AboutComponent();
    expect(component).toBeTruthy();
  });
});
