/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import * as moment      from 'moment';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { MonthService }            from './month.service';
import { CourseCalendarComponent } from './course-calendar.component';

beforeEachProviders(() => [MonthService, CourseCalendarComponent]);

describe('Component: CourseCalendar', () => {
  it('should create an instance', inject([CourseCalendarComponent], (app: CourseCalendarComponent) => {
    expect(app).toBeTruthy();
  }));

  it('should generate 36 months for the form', inject([CourseCalendarComponent], (app: CourseCalendarComponent) => {
    expect(app.months.length).toEqual(36);
  }));

  it('should have the correct previous, current and next months', inject([CourseCalendarComponent], (app: CourseCalendarComponent) => {
    let today = moment();

    expect(app.previousMonth.isSame(moment([today.year(), today.month() - 1, 1]))).toEqual(true);
    expect(app.currentMonth.isSame(moment([today.year(), today.month(), 1]))).toEqual(true);
    expect(app.nextMonth.isSame(moment([today.year(), today.month() + 1, 1]))).toEqual(true);
  }));
});
