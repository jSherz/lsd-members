/* tslint:disable:no-unused-variable */

import {
  ActivatedRoute,
  Params, UrlSegment, Router
} from '@angular/router';
import { RouterTestingModule } from '@angular/router/esm/testing/router_testing_module';
import { TestBed, inject } from '@angular/core/testing/test_bed';

import * as moment from 'moment';
import { Observable } from 'rxjs/Rx';

import { MonthService }            from '../month.service';
import { TileService }             from './tile/tile.service';
import { CourseCalendarComponent } from './course-calendar.component';
import { MOMENT_MATCHER }          from '../../../utils/moment-matcher';
import {
  CourseService,
  CourseWithNumSpaces, Course, CourseWithOrganisers, CourseSpaceWithMember
} from '../course.service';
import {APP_BASE_HREF} from "@angular/common";

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

  it('should show courses found with the course service to the relevant tiles', () => {
    // 28th September -> 07th November (inclusive) displayed
    let courses = [
      // Some courses before the tile dates
      new CourseWithNumSpaces(new Course('248be6b6-ddce-4788-a9ab-42edcd7c24d2', moment([2014, 6, 20]), '0b2d6da0-33f5-4709-bf8c-c0115c229fce', null, 0), 10, 2),
      new CourseWithNumSpaces(new Course('8699297a-bfd2-4393-bed3-5a04fce4f39e', moment([2014, 6, 27]), '1478f453-c0d9-4172-abba-17e9c0ef2503', 'c627038d-84c6-4182-9e77-59ad1951be28', 0), 8, 0),
      new CourseWithNumSpaces(new Course('30607353-cd7e-4a04-b857-dced01204ab3', moment([2014, 8, 8]),  '13584346-d49f-4196-85ee-c5abe32915d2', null, 0), 10, 4),
      new CourseWithNumSpaces(new Course('706507dc-36b2-49e7-99fc-7bc674c74f7c', moment([2014, 8, 18]), '060e2522-b51f-4476-8965-6b5901d58112', '8ae6a391-ff88-4709-9fa8-bb72e48f9532', 1), 8, 3),
      // Courses on the first and last tile dates
      new CourseWithNumSpaces(new Course('9a5a57a4-32a0-4221-a51a-6efc2f8be5bd', moment([2014, 6, 28]), '89f85263-5628-40c4-870a-d724a394a328', '5ed9bd40-36ee-4955-bfcc-c5d607c23273', 0), 7, 3),
      new CourseWithNumSpaces(new Course('09406555-a166-4fbf-8452-df526fc690d3', moment([2014, 8, 7]),  'ad1e3066-9298-48d2-81c2-2197a508699d', '4a3be841-8927-4f66-a655-25d224ded7c2', 1), 5, 0),
      // Other courses
      new CourseWithNumSpaces(new Course('adf5815d-33c9-4574-85bd-a6fa6495f3ac', moment([2014, 7, 8]),  '12d09f1c-f168-469f-803d-7a9047553113', null, 1), 9, 0),
      new CourseWithNumSpaces(new Course('8cdf7369-ab17-40d0-acfb-073a1f873c8d', moment([2014, 7, 14]), '09d719a0-5fbe-4534-9e3c-f80939a2459c', null, 1), 8, 1),
      new CourseWithNumSpaces(new Course('b9a9a1b1-29a6-40af-bb48-e2cc3fa83938', moment([2014, 7, 27]), '676b7664-4a4d-4ce4-b3a2-1c2caa492b74', 'e448e04e-1ec6-4418-a24a-f593db874878', 0), 8, 0)
    ];

    let app = mockComp(
      [['courses', {}, 'calendar', {'year': '2014', 'month': '8'}]],
      {'year': '2014', 'month': '8'},
      courses
    );

    let tileIndexesWithNoCourse = [
          1,  2,  3,  4,  5,  6,
      7,  8,  9,  10,     12, 13,
      14, 15, 16,     18, 19, 20,
      21, 22, 23, 24, 25, 26, 27,
      28, 29,     31, 32, 33, 34,
      35, 36, 37, 38, 39, 40
    ];

    tileIndexesWithNoCourse.map(index => expect(app.tiles[index].courses.length).toEqual(0));

    expect(app.tiles[0].courses[0].course.uuid).toEqual('9a5a57a4-32a0-4221-a51a-6efc2f8be5bd');
    expect(app.tiles[11].courses[0].course.uuid).toEqual('adf5815d-33c9-4574-85bd-a6fa6495f3ac');
    expect(app.tiles[17].courses[0].course.uuid).toEqual('8cdf7369-ab17-40d0-acfb-073a1f873c8d');
    expect(app.tiles[30].courses[0].course.uuid).toEqual('b9a9a1b1-29a6-40af-bb48-e2cc3fa83938');
    expect(app.tiles[41].courses[0].course.uuid).toEqual('09406555-a166-4fbf-8452-df526fc690d3');
  });

});

/**
 * A fake course service, used for testing.
 */
export class StubCourseService implements CourseService {

  get(uuid: string): Observable<CourseWithOrganisers> {
    return undefined;
  }

  spaces(uuid: string): Observable<CourseSpaceWithMember[]> {
    return undefined;
  }

  /**
   * The course service will return the given courses for any find() call.
   *
   * @param courses
   */
  constructor(private courses: CourseWithNumSpaces[]) {}

  find(startDate: moment.Moment, endDate: moment.Moment): Observable<CourseWithNumSpaces[]> {
    return Observable.of(this.courses);
  }

}

// Useful to mock a component with a given year & month
let mockComp = function(
  urlParts: [string, Params][] = [['courses', {}], ['calendar', {}]],
  params: Params = {},
  courses: CourseWithNumSpaces[] = []
): CourseCalendarComponent {
  return inject([Router], (router: Router) => {
    let urls = urlParts.map(([path, pathParams]) => new UrlSegment(path, pathParams));

    let observableUrls = Observable.of(urls);
    let observableParams: Observable<Params> = Observable.of(params);

    let activatedRoute = new ActivatedRoute();
    activatedRoute.url = observableUrls;
    activatedRoute.params = observableParams;

    let monthService = new MonthService();

    let tileService = new TileService();

    let courseService = new StubCourseService(courses);

    let component = new CourseCalendarComponent(monthService, router, activatedRoute, tileService, courseService);
    component.ngOnInit();

    return component;
  })();
};

beforeEach(() => {
  // TODO: Replace with RouterTestingModule.withRoutes when released
  TestBed.configureTestingModule({
    imports: [RouterTestingModule],
    providers: [
      { provide: APP_BASE_HREF, useValue: '/' },
      { provide: Router, useValue: {} }
    ]
  });

  jasmine.addMatchers(MOMENT_MATCHER)
});
