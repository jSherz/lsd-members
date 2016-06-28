/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { AdminBaseComponent } from './admin-base.component';

beforeEachProviders(() => [AdminBaseComponent]);

describe('Admin Base Component', () => {
  it('should create the app', inject([AdminBaseComponent], (app: AdminBaseComponent) => {
    expect(app).toBeTruthy();
  }));
});
