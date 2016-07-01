import { Injectable } from '@angular/core';
import * as moment from 'moment';

export const TILES_PER_CALENDAR = 42;

export class Tile {
  constructor (
    date: moment.Moment,
    isPreviousMonth: boolean,
    isNextMonth: boolean,
    isToday: boolean) {}
}

@Injectable()
export class TileService {

  constructor() {}

  getTiles(month: moment.Moment, currentDay: moment.Moment): Tile[] {
    let firstDay = this.firstDayOfMonth(month);

    let daysOfPrevMonth = this.numDaysOfPreviousMonth(firstDay.date());
    let daysOfPrimaryMonth = firstDay.daysInMonth();
    let daysOfNextMonth = TILES_PER_CALENDAR - daysOfPrimaryMonth - daysOfPrevMonth;

    let nextMonth = firstDay.add(1, 'months');

    return this.rangeOfDates(firstDay.subtract(daysOfPrevMonth, 'days'), daysOfPrevMonth).
      concat(this.rangeOfDates(firstDay, daysOfPrimaryMonth)).
      concat(this.rangeOfDates(nextMonth, daysOfNextMonth)).
      map(x => this.toTile(firstDay, nextMonth, currentDay, x));
  }

  private rangeOfDates(startingDate: moment.Moment, rangeSize: number): moment.Moment[] {
    return Array(rangeSize).fill(0).map((_, offset) => startingDate.add(offset, 'days'));
  }

  private numDaysOfPreviousMonth(dayOfWeek: number): number {
    // Show 7 days if it's a Monday
    if (dayOfWeek === 1 /* Monday */) {
      return 7;
    } else {
      // If not, show 1 for Tuesday, 2 for Wednesday, etc..
      return dayOfWeek - 1;
    }
  }

  private firstDayOfMonth(day: moment.Moment): moment.Moment {
    return moment([day.year(), day.month(), 1]);
  }

  private toTile(month: moment.Moment, nextMonth: moment.Moment, today: moment.Moment,
    inputDate: moment.Moment): Tile {
      let isPreviousMonth = inputDate.isBefore(month);
      let isNextMonth = inputDate.isSame(nextMonth) || inputDate.isAfter(nextMonth);
      let isToday = inputDate.isSame(today);

      return new Tile(inputDate, isPreviousMonth, isNextMonth, isToday);
    }
}
