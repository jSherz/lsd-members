/* tslint:disable:no-unused-variable */

import { Location             } from '@angular/common';
import { MockLocationStrategy } from '@angular/common/testing/mock_location_strategy';

import { ThankYouComponent } from './thank-you.component';

describe('Component: ThankYou', () => {

  it('should create an instance', () => {
    let location = new Location(new MockLocationStrategy());
    let component = new ThankYouComponent(location);

    expect(component).toBeTruthy();
  });

});
