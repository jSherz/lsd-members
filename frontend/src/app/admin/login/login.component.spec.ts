/* tslint:disable:no-unused-variable */

import { FormBuilder } from '@angular/forms';
import { Router      } from '@angular/router';

import { LoginComponent   } from './login.component';
import { LoginService     } from './login.service';
import { LoginServiceStub } from './login.service.stub';

class TestSetup {
  router: Router;
  service: LoginService;
  component: LoginComponent;

  constructor(router: Router, service: LoginService, component: LoginComponent) {
    this.router = router;
    this.service = service;
    this.component = component;
  }
}

function mockComp(): TestSetup {
  let keys = [];
  for (let key in Router.prototype) {
    if (Router.prototype.hasOwnProperty(key)) {
      keys.push(key);
    }
  }

  let builder = new FormBuilder();
  let router = jasmine.createSpyObj('MockRouter', keys);
  let service = new LoginServiceStub();

  return new TestSetup(router, service, new LoginComponent(builder, router, service));
}

describe('Component : Login', () => {

  it('shows a generic error message if the API call fails', () => {
    let test = mockComp();

    test.component.login({ email: LoginServiceStub.apiFailEmail, password: LoginServiceStub.apiFailPassword });
    expect(test.component.apiRequestFailed).toEqual(true);

    expect(test.router.navigate).not.toHaveBeenCalled();
  });

  it('clears the generic error message if a successful API call is made at a later time', () => {
    let test = mockComp();

    // Ensure error showing
    test.component.login({ email: LoginServiceStub.apiFailEmail, password: LoginServiceStub.apiFailPassword });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - successful response
    test.component.login({ email: LoginServiceStub.validEmail, password: LoginServiceStub.validPassword });
    expect(test.component.apiRequestFailed).toEqual(false);

    // Ensure error showing
    test.component.login({ email: LoginServiceStub.apiFailEmail, password: LoginServiceStub.apiFailPassword });
    expect(test.component.apiRequestFailed).toEqual(true);

    // API call - returned errors
    test.component.login({ email: LoginServiceStub.invalidEmail, password: LoginServiceStub.invalidPassword });
    expect(test.component.apiRequestFailed).toEqual(false);
  });

  it('navigates to the courses calendar if login succeeds', () => {
    let test = mockComp();

    test.component.login({ email: LoginServiceStub.validEmail, password: LoginServiceStub.validPassword });

    expect(test.router.navigate).toHaveBeenCalled();
  });

  it('shows errors against the correct field(s) with a successful API call but errors returned', () => {
    let test = mockComp();

    test.component.login({ email: LoginServiceStub.accountLockedEmail, password: LoginServiceStub.accountLockedPassword });
    expect(test.component.errors[0].password).toEqual('Your account has been disabled. Please contact an administrator.');

    expect(test.router.navigate).not.toHaveBeenCalled();
  });

});
