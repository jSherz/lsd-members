/* tslint:disable:no-unused-variable */

import {
  ActivatedRoute,
  UrlPathWithParams,
  Params,
  Data
} from '@angular/router';
import * as moment from 'moment';
import { Observable } from 'rxjs/Rx';

import { MonthService }            from './month.service';
import { TileService }             from './tile.service';
import { CourseCalendarComponent } from './course-calendar.component';
import { MOMENT_MATCHER }          from '../../utils/moment-matcher';
import {CourseService, CourseWithNumSpaces} from "./course.service";

export class StubCourseService implements CourseService {
  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    return undefined;
  }
}

// Useful to mock a component with a given year & month
let mockComp = function(
  urlParts: [string, Params][] = [['courses', {}], ['calendar', {}]],
  params: Params = {}
): CourseCalendarComponent {
  let urls = urlParts.map(([path, pathParams]) => new UrlPathWithParams(path, pathParams));

  let observableUrls = Observable.of(urls);
  let observableParams: Observable<Params> = Observable.of(params);

  let activatedRoute = new ActivatedRoute();
  activatedRoute.url = observableUrls;
  activatedRoute.params = observableParams;

  let monthService = new MonthService();

  let tileService = new TileService();

  let courseService = new StubCourseService();

  let component = new CourseCalendarComponent(monthService, activatedRoute, tileService, courseService);
  component.ngOnInit();

  return component;
};

beforeEach(() => jasmine.addMatchers(MOMENT_MATCHER));

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

    expect(app.previousMonth).toBeSameAs(moment([today.year(), today.month() - 1, 1]));
    expect(app.currentMonth).toBeSameAs(moment([today.year(), today.month(), 1]));
    expect(app.nextMonth).toBeSameAs(moment([today.year(), today.month() + 1, 1]));
  });

  it('should show the specified year and month if provided', () => {
    let app = mockComp(
      [['courses', {}, 'calendar', {'year': '2017', 'month': '5'}]],
      {'year': '2017', 'month': '5'}
    );

    expect(app.previousMonth).toBeSameAs(moment([2017, 3, 1]));
    expect(app.currentMonth).toBeSameAs(moment([2017, 4, 1]));
    expect(app.nextMonth).toBeSameAs(moment([2017, 5, 1]));
  });
});
