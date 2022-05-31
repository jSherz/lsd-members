/* eslint-disable @typescript-eslint/no-unused-vars */

import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";
import { async, inject } from "@angular/core/testing";
import { HomeComponent } from "./home.component";

describe("Component: Home", () => {
  it("should not show the video by default", () => {
    const component = new HomeComponent();
    expect(component).toBeTruthy();
    expect(component.showVideo).toBeFalsy();
  });

  it("should show the video when requested", () => {
    const component = new HomeComponent();
    component.loadVideo();

    expect(component.showVideo).toBeTruthy();
  });
});
