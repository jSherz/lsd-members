/* tslint:disable:no-unused-variable */

import {async} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';

import {SignupAltComponent} from './signup-alt.component';
import {SignupServiceStub} from '../service/signup.service.stub';
import {SignupService} from '../service/signup.service';

class TestSetup {
  router: Router;
  service: SignupService;
  component: SignupAltComponent;

  constructor(router: Router, service: SignupService, component: SignupAltComponent) {
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
  const router = jasmine.createSpyObj('MockRouter', keys);
  const service = new SignupServiceStub();

  return new TestSetup(router, service, new SignupAltComponent(builder, router, service));
}

describe('Signup Alt Component', () => {

  it('shows a generic error message if the API call fails', async(() => {
    const test = mockComp();

    test.component.signup({name: SignupServiceStub.apiFailName, email: SignupServiceStub.apiFailEmail});
    expect(test.component.apiRequestFailed).toEqual(true);

    expect(test.router.navigate).not.toHaveBeenCalled();
  }));

  it('clears the generic error message if a successful API call is made at a later time', async(() => {
    const test = mockComp();

    // Ensure error showing
    test.component.signup({name: SignupServiceStub.apiFailName, email: SignupServiceStub.apiFailEmail});
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - successful response
    test.component.signup({name: SignupServiceStub.validName, email: SignupServiceStub.validEmail});
    expect(test.component.apiRequestFailed).toEqual(false);

    // Ensure error showing
    test.component.signup({name: SignupServiceStub.apiFailName, email: SignupServiceStub.apiFailEmail});
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - returned errors
    test.component.signup({name: SignupServiceStub.inUseName, email: SignupServiceStub.inUseEmail});
    expect(test.component.apiRequestFailed).toEqual(false);
  }));

  it('navigates to the thank-you page if signing up succeeds', async(() => {
    const test = mockComp();

    test.component.signup({name: SignupServiceStub.validName, email: SignupServiceStub.validEmail});

    expect(test.router.navigate).toHaveBeenCalled();
  }));

  it('shows errors against the correct field(s) with a successful API call but errors returned', async(() => {
    const test = mockComp();

    test.component.signup({name: SignupServiceStub.inUseName, email: SignupServiceStub.inUseEmail});
    expect(test.component.errors[0].email).toEqual('The specified e-mail is in use');

    expect(test.router.navigate).not.toHaveBeenCalled();
  }));

});
