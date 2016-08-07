/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { Location, PathLocationStrategy, LocationStrategy } from '@angular/common';
import { APP_ROUTER_PROVIDERS } from './../../index';
import { ThankYouComponent } from './thank-you.component';

describe('Component: ThankYou', () => {
  beforeEachProviders(() => [APP_ROUTER_PROVIDERS])

  it('should create an instance', inject([LocationStrategy], (locationStrategy: LocationStrategy) => {
    let location = new Location(locationStrategy);
    let component = new ThankYouComponent(location);
    expect(component).toBeTruthy();
  }));
});
