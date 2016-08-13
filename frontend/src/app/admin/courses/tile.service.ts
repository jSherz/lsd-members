import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {CourseWithNumSpaces} from './course.service';

export const TILES_PER_CALENDAR = 42;

export class Tile {
  date: moment.Moment;
  isPreviousMonth: boolean;
  isNextMonth: boolean;
  isToday: boolean;
  courses: CourseWithNumSpaces[] = [];

  constructor (
    date: moment.Moment,
    isPreviousMonth: boolean,
    isNextMonth: boolean,
    isToday: boolean) {
      this.date = date;
      this.isPreviousMonth = isPreviousMonth;
      this.isNextMonth = isNextMonth;
      this.isToday = isToday;
    }
}

@Injectable()
export class TileService {

  constructor() {}

  getTiles(month: moment.Moment, rawCurrentDay: moment.Moment): Tile[] {
    let firstDay = this.firstDayOfMonth(month);
    let currentDay = this.stripTime(rawCurrentDay);

    let daysOfPrevMonth = this.numDaysOfPreviousMonth(firstDay.day());
    let daysOfPrimaryMonth = firstDay.daysInMonth();
    let daysOfNextMonth = TILES_PER_CALENDAR - daysOfPrimaryMonth - daysOfPrevMonth;

    let nextMonth = firstDay.clone().add(1, 'months');

    return this.rangeOfDates(firstDay.clone().subtract(daysOfPrevMonth, 'days'), daysOfPrevMonth).
      concat(this.rangeOfDates(firstDay, daysOfPrimaryMonth)).
      concat(this.rangeOfDates(nextMonth, daysOfNextMonth)).
      map(x => this.toTile(firstDay, nextMonth, currentDay, x));
  }

  private rangeOfDates(startingDate: moment.Moment, rangeSize: number): moment.Moment[] {
    return Array(rangeSize).fill(0).map((_, offset) => startingDate.clone().add(offset, 'days'));
  }

  private numDaysOfPreviousMonth(dayOfWeek: number): number {
    switch (dayOfWeek) {
      case 0: return 6; // Sunday
      case 1: return 7; // Monday
      case 2: return 1;
      case 3: return 2;
      case 4: return 3;
      case 5: return 4;
      case 6: return 5; // Saturday
    }
  }

  private firstDayOfMonth(day: moment.Moment): moment.Moment {
    return moment([day.year(), day.month(), 1]);
  }

  private stripTime(input: moment.Moment): moment.Moment {
    return moment([input.year(), input.month(), input.date()]);
  }

  private toTile(month: moment.Moment, nextMonth: moment.Moment, today: moment.Moment,
    inputDate: moment.Moment): Tile {

    let isPreviousMonth = inputDate.isBefore(month);
    let isNextMonth = inputDate.isSame(nextMonth) || inputDate.isAfter(nextMonth);
    let isToday = inputDate.isSame(today);

    return new Tile(inputDate, isPreviousMonth, isNextMonth, isToday);
  }
}
