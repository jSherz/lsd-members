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

  constructor(private monthService: MonthService) {}

  months = this.monthService.get(moment());

  ngOnInit() { }

}
