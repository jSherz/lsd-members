/* tslint:disable:no-unused-variable */

import { async } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";

import { LoginComponent } from "./login.component";
import { LoginService } from "./login.service";
import { LoginServiceStub } from "./login.service.stub";
import { StubApiKeyService } from "../utils/api-key.service.stub";

class TestSetup {
  router: Router;
  service: LoginService;
  component: LoginComponent;

  constructor(
    router: Router,
    service: LoginService,
    component: LoginComponent
  ) {
    this.router = router;
    this.service = service;
    this.component = component;
  }
}

function mockComp(): TestSetup {
  const keys = [];
  for (const key in Router.prototype) {
    if (Router.prototype.hasOwnProperty(key)) {
      keys.push(key);
    }
  }

  const builder = new FormBuilder();
  const router = jasmine.createSpyObj("MockRouter", keys);
  const service = new LoginServiceStub();
  const apiKeyService = new StubApiKeyService(undefined);

  return new TestSetup(
    router,
    service,
    new LoginComponent(builder, router, service, apiKeyService)
  );
}

describe("Component: Login", () => {
  it("shows a generic error message if the API call fails", async(() => {
    const test = mockComp();

    test.component.login({
      email: LoginServiceStub.apiFailEmail,
      password: LoginServiceStub.apiFailPassword
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    expect(test.router.navigate).not.toHaveBeenCalled();
  }));

  it("clears the generic error message if a successful API call is made at a later time", async(() => {
    const test = mockComp();

    // Ensure error showing
    test.component.login({
      email: LoginServiceStub.apiFailEmail,
      password: LoginServiceStub.apiFailPassword
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - successful response
    test.component.login({
      email: LoginServiceStub.validEmail,
      password: LoginServiceStub.validPassword
    });
    expect(test.component.apiRequestFailed).toEqual(false);

    // Ensure error showing
    test.component.login({
      email: LoginServiceStub.apiFailEmail,
      password: LoginServiceStub.apiFailPassword
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - returned errors
    test.component.login({
      email: LoginServiceStub.invalidEmail,
      password: LoginServiceStub.invalidPassword
    });
    expect(test.component.apiRequestFailed).toEqual(false);
  }));

  it("navigates to the courses calendar if login succeeds", async(() => {
    const test = mockComp();

    test.component.login({
      email: LoginServiceStub.validEmail,
      password: LoginServiceStub.validPassword
    });

    expect(test.router.navigate).toHaveBeenCalled();
  }));

  it("shows errors against the correct field(s) with a successful API call but errors returned", async(() => {
    const test = mockComp();

    test.component.login({
      email: LoginServiceStub.accountLockedEmail,
      password: LoginServiceStub.accountLockedPassword
    });
    expect(test.component.errors.email).toEqual("error.accountLocked");

    expect(test.router.navigate).not.toHaveBeenCalled();
  }));
});
