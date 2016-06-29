import { Component, OnInit } from '@angular/core';
import * as moment           from 'moment';

@Component({
  moduleId: module.id,
  templateUrl: 'course-calendar.component.html'
})
export class CourseCalendarComponent implements OnInit {

  private startMonth = moment().subtract(1, 'years');

  constructor() {}

  months: moment.Moment[] = [...Array(36)].map((_, i) =>
    this.startMonth.add(i + 1, 'months'));

  ngOnInit() { }

}
