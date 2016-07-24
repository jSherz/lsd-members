/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { SignupComponent } from './signup.component';

beforeEachProviders(() => [FormBuilder, SignupComponent]);

describe('LSD.SignupComponent', () => {
  it('should create the app', inject([SignupComponent], (app: SignupComponent) => {
    expect(app).toBeTruthy();
  }));
});
