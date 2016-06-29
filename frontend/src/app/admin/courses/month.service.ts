import { Injectable } from '@angular/core';
import * as moment    from 'moment';

@Injectable()
export class MonthService {
  private range(size: Number): Number[] {
    return Array(size).fill(0).map((_, i) => i + 1)
  }

  get(currentMonth: moment.Moment): moment.Moment[] {
    let currentMonthStripped = moment([currentMonth.year(), currentMonth.month(), 1]);
    let startMonth = currentMonthStripped.subtract(1, 'years').subtract(1, 'months');

    return this.range(36).map(
      (offset) => startMonth.clone().add(offset.toString(), 'months')
    );
  }
}
