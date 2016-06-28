/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { SignupService } from './signup.service';

describe('Signup Service', () => {
  beforeEachProviders(() => [SignupService]);

  it('should ...',
      inject([SignupService], (service: SignupService) => {
    expect(service).toBeTruthy();
  }));
});
