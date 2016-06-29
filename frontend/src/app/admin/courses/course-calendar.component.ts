import { Component, OnInit } from '@angular/core';
import * as moment           from 'moment';
import { MonthService }      from './month.service';

@Component({
  moduleId: module.id,
  templateUrl: 'course-calendar.component.html',
  providers: [MonthService]
})
export class CourseCalendarComponent implements OnInit {

  private startMonth = moment().subtract(1, 'years');

  private today = moment();

  constructor(private monthService: MonthService) { }

  months = this.monthService.get(moment());

  currentMonth = moment([this.today.year(), this.today.month(), 1])

  previousMonth = this.currentMonth.clone().subtract(1, 'months')

  nextMonth = this.currentMonth.clone().add(1, 'months')

  ngOnInit() { }

}
