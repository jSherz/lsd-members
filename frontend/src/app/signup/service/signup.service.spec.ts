/* tslint:disable:no-unused-variable */

import {
  inject
} from '@angular/core/testing';
import { SignupService  } from './signup.service';
import { addProviders   } from '@angular/core/testing/testing';
import { HTTP_PROVIDERS } from '@angular/http';

describe('Signup Service', () => {
  beforeEach(() => addProviders([HTTP_PROVIDERS, SignupService]));

  it('should ...',
      inject([SignupService], (service: SignupService) => {
    expect(service).toBeTruthy();
  }));
});
