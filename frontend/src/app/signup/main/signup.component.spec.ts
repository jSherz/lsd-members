/* tslint:disable:no-unused-variable */

import { inject, addProviders   } from '@angular/core/testing';
import { FormBuilder            } from '@angular/forms';
import { APP_BASE_HREF          } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { APP_ROUTER_PROVIDERS   } from '../../app.routes';
import { SignupComponent        } from './signup.component';
import {SignupService} from "../../../../signup.service";
import {SignupServiceStub} from "../service/signup.service.stub";

beforeEach(() => addProviders([
  APP_ROUTER_PROVIDERS,
  {provide: APP_BASE_HREF, useValue: '/'},
  {provide: ActivatedRoute, useValue: {}},
  {provide: Router, useValue: {}},
  FormBuilder,
  SignupComponent
]));

describe('SignupComponent', () => {

  it('shows a generic error message if the API call fails', () => {
    let test = mockComp();

    test.component.signup({ name: SignupServiceStub.apiFailName, phoneNumber: SignupServiceStub.apiFailPhoneNumber });
    expect(test.component.apiRequestFailed).toEqual(true);

    expect(test.router.navigate).not.toHaveBeenCalled();
  });

  it('clears the generic error message if a successful API call is made at a later time', () => {
    let test = mockComp();

    // Ensure error showing
    test.component.signup({ name: SignupServiceStub.apiFailName, phoneNumber: SignupServiceStub.apiFailPhoneNumber });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - successful response
    test.component.signup({ name: SignupServiceStub.validName, phoneNumber: SignupServiceStub.validPhoneNumber });
    expect(test.component.apiRequestFailed).toEqual(false);

    // Ensure error showing
    test.component.signup({ name: SignupServiceStub.apiFailName, phoneNumber: SignupServiceStub.apiFailPhoneNumber });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - returned errors
    test.component.signup({ name: SignupServiceStub.inUseName, phoneNumber: SignupServiceStub.inUsePhoneNumber });
    expect(test.component.apiRequestFailed).toEqual(false);
  });

  it('navigates to the thank-you page if signing up succeeds', () => {
    let test = mockComp();

    test.component.signup({ name: SignupServiceStub.validName, phoneNumber: SignupServiceStub.validPhoneNumber });

    expect(test.router.navigate).toHaveBeenCalled();
  });

  it('shows errors against the correct field(s) with a successful API call but errors returned', () => {
    let test = mockComp();

    test.component.signup({ name: SignupServiceStub.inUseName, phoneNumber: SignupServiceStub.inUsePhoneNumber });
    expect(test.component.errors[0].phoneNumber).toEqual("The specified phone number is in use");

    expect(test.router.navigate).not.toHaveBeenCalled();
  });

});

/**
 * A holder class for test fixtures.
 */
class TestSetup {
  router: Router;
  service: SignupService;
  component: SignupComponent;

  constructor(router: Router, service: SignupService, component: SignupComponent) {
    this.router = router;
    this.service = service;
    this.component = component;
  }
}

/**
 * Create a new component with mocked router, stubbed service and a real form builder.
 *
 * @returns {TestSetup}
 */
function mockComp(): TestSetup {
  let keys = [];
  for (let key in Router.prototype) {
    keys.push(key);
  }

  let builder = new FormBuilder();
  let router = jasmine.createSpyObj('MockRouter', keys);
  let service = new SignupServiceStub();

  return new TestSetup(router, service, new SignupComponent(builder, router, service));
}
