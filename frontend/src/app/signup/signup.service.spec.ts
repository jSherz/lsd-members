/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { SignupServiceService } from './signup-service.service';

describe('SignupService Service', () => {
  beforeEachProviders(() => [SignupServiceService]);

  it('should ...',
      inject([SignupServiceService], (service: SignupServiceService) => {
    expect(service).toBeTruthy();
  }));
});
