import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';

@Component({
  moduleId: module.id,
  selector: 'app-course-calendar',
  templateUrl: 'course-calendar.component.html'
})
export class CourseCalendarComponent implements OnInit {

  constructor() {}

  private startMonth = moment().subtract(1, 'years');

  months: moment.Moment[] = [...Array(36)].map((_, i) =>
    this.startMonth.add(i + 1, 'months'))

  ngOnInit() {
    /*
    @for(offset <- 1 to 36) {
      <option value="0">@{startDate.plus(Period.months(offset)).toString("yyyy - MMMM")}</option>
    }
    */
  }

}
