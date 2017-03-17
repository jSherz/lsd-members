/* tslint:disable:no-unused-variable */

import {Component} from '@angular/core';
import {inject, async, TestBed} from '@angular/core/testing';
import {NavigationStart, Event, Router} from '@angular/router';
import {RouterTestingModule} from '@angular/router/testing';
import {Subject, Observable} from 'rxjs';

import {AppComponent} from '../../app.component';
import {BaseComponent} from './base.component';


describe('Component: Base', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent
      ],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'home', component: AppComponent }
        ])
      ]
    });
  });

  it('should collapse the menu when the route changes', async(inject([Router], (router) => {
    let subject: Subject<NavigationStart> = new Subject();
    router.events = subject;
    let component = new BaseComponent(router);

    component.menuCollapsed = false;

    expect(component.menuCollapsed).toBeFalsy();

    subject.next(new NavigationStart(123, 'test-page'));

    expect(component.menuCollapsed).toBeTruthy();
  })));

});
