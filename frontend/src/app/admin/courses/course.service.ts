import { Injectable } from '@angular/core';
import * as moment    from 'moment';

export class Course {
  constructor (date: moment.Moment, totalSpace: number, openSpaces: number) { }
}

@Injectable()
export class CourseService {

  constructor() {}

  course(startDate: moment.Moment, endDate: moment.Moment) {
    return [
      new Course(moment([startDate.year(), startDate.month(), 10]), 10, 3),
      new Course(moment([startDate.year(), startDate.month(), 15]), 8, 0),
      new Course(moment([startDate.year(), startDate.month(), 16]), 8, 1),
      new Course(moment([startDate.year(), startDate.month(), 25]), 8, 2)
    ];
  }

}
