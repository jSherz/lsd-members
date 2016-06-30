import { Component, OnInit }                 from '@angular/core';
import { ROUTER_DIRECTIVES, ActivatedRoute } from '@angular/router';
import { Subscription }                      from 'rxjs/Subscription';
import * as moment                           from 'moment';
import { MonthService }                      from './month.service';

@Component({
  moduleId: module.id,
  templateUrl: 'course-calendar.component.html',
  providers: [MonthService],
  directives: [ROUTER_DIRECTIVES]
})
export class CourseCalendarComponent implements OnInit {

  private startMonth = moment().subtract(1, 'years');

  private displayMonth = moment();

  private displayMonthSub: Subscription;

  months: moment.Moment[];
  currentMonth: moment.Moment;
  previousMonth: moment.Moment;
  nextMonth: moment.Moment;

  constructor(private monthService: MonthService, private route: ActivatedRoute) { }

  private updateCalendar() {
    this.months = this.monthService.get(this.displayMonth);

    this.currentMonth = moment([this.displayMonth.year(), this.displayMonth.month(), 1])

    this.previousMonth = this.currentMonth.clone().subtract(1, 'months')
    this.nextMonth = this.currentMonth.clone().add(1, 'months')
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
