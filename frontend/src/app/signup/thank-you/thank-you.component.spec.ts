/* tslint:disable:no-unused-variable */

import { Location, LocationStrategy } from '@angular/common';
import { APP_ROUTER_PROVIDERS } from './../../index';
import { ThankYouComponent } from './thank-you.component';
import { addProviders } from '@angular/core/testing/testing';
import { inject } from '@angular/core/testing/test_injector';

describe('Component: ThankYou', () => {
  beforeEach(() => addProviders([APP_ROUTER_PROVIDERS]));

  it('should create an instance', inject([LocationStrategy], (locationStrategy: LocationStrategy) => {
    let location = new Location(locationStrategy);
    let component = new ThankYouComponent(location);
    expect(component).toBeTruthy();
  }));
});
