/* tslint:disable:no-unused-variable */

import { By }                                from '@angular/platform-browser';
import { DebugElement }                      from '@angular/core';
import { ActivatedRoute, UrlPathWithParams } from '@angular/router';
import * as moment                           from 'moment';
import { Observable }                        from 'rxjs/Rx';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,

  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { MonthService }            from './month.service';
import { TileService }             from './tile.service';
import { CourseCalendarComponent } from './course-calendar.component';

type UrlParams = { [key: string]: any; }

// Useful to mock a component with a given year & month
let mockComp = function(
  urlParts: [string, UrlParams][] = [['courses', {}], ['calendar', {}]],
  params: UrlParams = {}
): CourseCalendarComponent {
  let urls = urlParts.map(([path, pathParams]) => new UrlPathWithParams(path, pathParams));

  let observableUrls = Observable.of(urls);
  let observableParams = Observable.of(params);

  let activatedRoute =
    new ActivatedRoute(observableUrls, observableParams, undefined, undefined, undefined);

  let monthService = new MonthService();

  let tileService = new TileService();

  let component = new CourseCalendarComponent(monthService, activatedRoute, tileService);
  component.ngOnInit();

  return component;
};

describe('Component: CourseCalendar', () => {
  it('should create an instance', () => {
    let app = mockComp();
    expect(app).toBeTruthy();
  });

  it('should generate 36 months for the form', () => {
    let app = mockComp();
    expect(app.months.length).toEqual(36);
  });

  it('should have the correct previous, current and next months (no month specified)', () => {
    let app = mockComp();
    let today = moment();

    expect(app.previousMonth.isSame(moment([today.year(), today.month() - 1, 1]))).toEqual(true);
    expect(app.currentMonth.isSame(moment([today.year(), today.month(), 1]))).toEqual(true);
    expect(app.nextMonth.isSame(moment([today.year(), today.month() + 1, 1]))).toEqual(true);
  });

  it('should show the specified year and month if provided', () => {
    let app = mockComp(
      [['courses', {}, 'calendar', {'year': '2017', 'month': '5'}]],
      {'year': '2017', 'month': '5'}
    );

    expect(app.previousMonth.isSame(moment([2017, 3, 1]))).toEqual(true);
    expect(app.currentMonth.isSame(moment([2017, 4, 1]))).toEqual(true);
    expect(app.nextMonth.isSame(moment([2017, 5, 1]))).toEqual(true);
  });
});
