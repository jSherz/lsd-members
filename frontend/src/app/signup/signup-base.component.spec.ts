/* tslint:disable:no-unused-variable */

import {
  inject
} from '@angular/core/testing';
import { SignupBaseComponent } from './signup-base.component';
import { addProviders        } from '@angular/core/testing/testing';

beforeEach(() => addProviders([SignupBaseComponent]));

describe('Signup Base Component', () => {
  it('should create the app', inject([SignupBaseComponent], (app: SignupBaseComponent) => {
    expect(app).toBeTruthy();
  }));
});
