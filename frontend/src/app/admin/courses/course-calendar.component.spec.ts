/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { CourseCalendarComponent } from './course-calendar.component';

describe('Component: CourseCalendar', () => {
  it('should create an instance', () => {
    let component = new CourseCalendarComponent();
    expect(component).toBeTruthy();
  });

  it('should generate 36 months for the form', inject([CourseCalendarComponent], (app: CourseCalendarComponent) => {
    expect(app.months.length).toEqual(36);
  }));
});
