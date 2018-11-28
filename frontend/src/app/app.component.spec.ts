/* tslint:disable:no-unused-variable */

import {async} from '@angular/core/testing';

import {AppComponent} from './app.component';


describe('Component: App', () => {

  it('should create the app', async(() => {
    const app = new AppComponent({
      startTracking: () => null,
    } as any);
    expect(app).toBeTruthy();
  }));

});
