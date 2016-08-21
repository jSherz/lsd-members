import {Component, OnInit, OnDestroy}      from '@angular/core';
import {ROUTER_DIRECTIVES, ActivatedRoute, Router} from '@angular/router';
import {Subscription}      from 'rxjs/Subscription';
import * as moment         from 'moment';
import {MonthService}      from '../month.service';
import {Tile, TileService} from './tile/tile.service';
import {TileComponent}     from './tile/tile.component';
import {
  CourseService,
  CourseWithNumSpaces,
  CourseServiceImpl
} from '../course.service';

@Component({
  selector: 'course-calendar-component',
  templateUrl: 'course-calendar.component.html',
  providers: [MonthService, TileService,  { provide: CourseService, useClass: CourseServiceImpl }],
  directives: [ROUTER_DIRECTIVES, TileComponent]
})
export class CourseCalendarComponent implements OnInit, OnDestroy {

  private displayMonth = moment();

  private displayMonthSub: Subscription;

  months: moment.Moment[];
  currentMonth: moment.Moment;
  previousMonth: moment.Moment;
  nextMonth: moment.Moment;
  tiles: Tile[];
  courses: CourseWithNumSpaces[];
  errorMessage: any;

  constructor(
    private monthService: MonthService,
    private router: Router,
    private route: ActivatedRoute,
    private tileService: TileService,
    private courseService: CourseService) { }

  private updateCalendar() {
    this.months = this.monthService.get(this.displayMonth);

    this.currentMonth = moment([this.displayMonth.year(), this.displayMonth.month(), 1]);

    this.previousMonth = this.currentMonth.clone().subtract(1, 'months');
    this.nextMonth = this.currentMonth.clone().add(1, 'months');

    this.tiles = this.tileService.getTiles(this.currentMonth, moment());

    this.courseService.find(this.tiles[0].date, this.tiles[this.tiles.length - 1].date).subscribe(
      courses => {
        this.courses = courses;

        for (let tile of this.tiles) {
          tile.courses = [];

          for (let course of this.courses) {
            if (tile.date.isSame(course.course.date)) {
              tile.courses.push(course);
            }
          }
        }
      },
      error => this.errorMessage = error
    );
  }

  /**
   * Called when the month selection is changed.
   *
   * Navigates the user to the newly selected month, if we're not currently viewing it.
   *
   * @param event
   */
  private monthSelectionChanged(event) {
    let chosenMonth = moment(event.target.value);

    if (!chosenMonth.isSame(this.currentMonth)) {
      let year = chosenMonth.year();
      let month = chosenMonth.month() + 1; // Month is zero indexed, URL is 1 indexed

      this.router.navigate(['admin', 'courses', 'calendar', year, month]);
    }
  }

  ngOnInit() {
    this.displayMonthSub = this.route.params
      .subscribe(params => {
        let year: number = +params['year'];
        let month: number = +params['month'];

        if (!isNaN(year) && !isNaN(month)) {
          // Convert 1 indexed month to 0 indexed for momentjs / JS
          let zeroIndexedMonth: number = month - 1;
          this.displayMonth = moment([year, zeroIndexedMonth, 1]);
          this.updateCalendar();
        }
      });

    this.updateCalendar();
  }

  ngOnDestroy() {
    this.displayMonthSub.unsubscribe();
  }

}