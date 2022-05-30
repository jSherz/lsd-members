/* eslint-disable @typescript-eslint/no-unused-vars */

import { async } from "@angular/core/testing";

import { AppComponent } from "./app.component";

describe("Component: App", () => {
  it("should create the app", async(() => {
    const app = new AppComponent();
    expect(app).toBeTruthy();
  }));
});
