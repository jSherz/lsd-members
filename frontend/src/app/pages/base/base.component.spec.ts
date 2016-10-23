/* tslint:disable:no-unused-variable */

import {async} from '@angular/core/testing';
import {Router, NavigationStart, Event} from '@angular/router';
import {Subject} from 'rxjs';

import {BaseComponent} from './base.component';


describe('Component: Base', () => {

  it('should collapse the menu when the route changes', async(() => {
    let fakeRouter = new FakeRouter();
    let component = new BaseComponent(fakeRouter);

    component.menuCollapsed = false;

    expect(component.menuCollapsed).toBeFalsy();

    fakeRouter.triggerSubscriptions();

    expect(component.menuCollapsed).toBeTruthy();
  }));

});

class FakeRouter extends Router {

  constructor() {
    super(null, null, null, null, null, null, null, []);
  }

  triggerSubscriptions() {
    let navigationStart: Event = new NavigationStart(123, 'test-page');
    (<Subject<Event>>this.events).next(navigationStart);
  }

}
