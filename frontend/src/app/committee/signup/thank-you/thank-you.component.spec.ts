/* tslint:disable:no-unused-variable */

import {Location} from '@angular/common';
import {MockLocationStrategy} from '@angular/common/testing';

import {ThankYouComponent} from './thank-you.component';

describe('Component: ThankYou', () => {

  it('should create an instance', () => {
    const location = new Location(new MockLocationStrategy());
    const component = new ThankYouComponent(location);

    expect(component).toBeTruthy();
  });

});
