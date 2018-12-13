/* tslint:disable:no-unused-variable */

import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";

import { SignupComponent } from "./signup.component";
import { SignupServiceStub } from "../service/signup.service.stub";
import { SignupService } from "../service/signup.service";

class TestSetup {
  router: Router;
  service: SignupService;
  component: SignupComponent;

  constructor(
    router: Router,
    service: SignupService,
    component: SignupComponent
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
  const service = new SignupServiceStub();

  return new TestSetup(
    router,
    service,
    new SignupComponent(builder, router, service)
  );
}

describe("SignupComponent", () => {
  it("shows a generic error message if the API call fails", () => {
    const test = mockComp();

    test.component.signup({
      name: SignupServiceStub.apiFailName,
      phoneNumber: SignupServiceStub.apiFailPhoneNumber
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    expect(test.router.navigate).not.toHaveBeenCalled();
  });

  it("clears the generic error message if a successful API call is made at a later time", () => {
    const test = mockComp();

    // Ensure error showing
    test.component.signup({
      name: SignupServiceStub.apiFailName,
      phoneNumber: SignupServiceStub.apiFailPhoneNumber
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - successful response
    test.component.signup({
      name: SignupServiceStub.validName,
      phoneNumber: SignupServiceStub.validPhoneNumber
    });
    expect(test.component.apiRequestFailed).toEqual(false);

    // Ensure error showing
    test.component.signup({
      name: SignupServiceStub.apiFailName,
      phoneNumber: SignupServiceStub.apiFailPhoneNumber
    });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - returned errors
    test.component.signup({
      name: SignupServiceStub.inUseName,
      phoneNumber: SignupServiceStub.inUsePhoneNumber
    });
    expect(test.component.apiRequestFailed).toEqual(false);
  });

  it("navigates to the thank-you page if signing up succeeds", () => {
    const test = mockComp();

    test.component.signup({
      name: SignupServiceStub.validName,
      phoneNumber: SignupServiceStub.validPhoneNumber
    });

    expect(test.router.navigate).toHaveBeenCalled();
  });

  it("shows errors against the correct field(s) with a successful API call but errors returned", () => {
    const test = mockComp();

    test.component.signup({
      name: SignupServiceStub.inUseName,
      phoneNumber: SignupServiceStub.inUsePhoneNumber
    });
    expect(test.component.errors[0].phoneNumber).toEqual(
      "The specified phone number is in use"
    );

    expect(test.router.navigate).not.toHaveBeenCalled();
  });
});
