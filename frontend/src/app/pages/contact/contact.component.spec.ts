/* eslint-disable @typescript-eslint/no-unused-vars */

import { By } from "@angular/platform-browser";
import { DebugElement } from "@angular/core";
import { async, inject } from "@angular/core/testing";
import { ContactComponent } from "./contact.component";

describe("Component: Contact", () => {
  it("should create an instance", () => {
    const component = new ContactComponent();
    expect(component).toBeTruthy();
  });
});
