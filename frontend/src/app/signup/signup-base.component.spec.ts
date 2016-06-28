/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { SignupBaseComponent } from './signup-base.component';

beforeEachProviders(() => [SignupBaseComponent]);

describe('Signup Base Component', () => {
  it('should create the app', inject([SignupBaseComponent], (app: SignupBaseComponent) => {
    expect(app).toBeTruthy();
  }));
});
