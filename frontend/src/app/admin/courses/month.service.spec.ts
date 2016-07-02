/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import * as moment        from 'moment';
import { Moment }         from 'moment';
import { MonthService }   from './month.service';
import { MOMENT_MATCHER } from '../../utils/moment-matcher';

describe('Month Service', () => {
  beforeEachProviders(() => [MonthService]);

  beforeEach(() => jasmine.addMatchers(MOMENT_MATCHER));

  let testDate = moment();

  it('should return 36 moments', inject([MonthService], (service: MonthService) => {
    let result: moment.Moment[] = service.get(testDate);

    expect(result.length).toEqual(36);
  }));

  it('should return the correct months', inject([MonthService], (service: MonthService) => {
    let expected = [
      moment([2014, 4, 1]),  moment([2014, 5, 1]),  moment([2014, 6, 1]),
      moment([2014, 7, 1]),  moment([2014, 8, 1]),  moment([2014, 9, 1]),
      moment([2014, 10, 1]), moment([2014, 11, 1]), moment([2015, 0, 1]),
      moment([2015, 1, 1]),  moment([2015, 2, 1]),  moment([2015, 3, 1]),
      moment([2015, 4, 1]),  moment([2015, 5, 1]),  moment([2015, 6, 1]),
      moment([2015, 7, 1]),  moment([2015, 8, 1]),  moment([2015, 9, 1]),
      moment([2015, 10, 1]), moment([2015, 11, 1]), moment([2016, 0, 1]),
      moment([2016, 1, 1]),  moment([2016, 2, 1]),  moment([2016, 3, 1]),
      moment([2016, 4, 1]),  moment([2016, 5, 1]),  moment([2016, 6, 1]),
      moment([2016, 7, 1]),  moment([2016, 8, 1]),  moment([2016, 9, 1]),
      moment([2016, 10, 1]), moment([2016, 11, 1]), moment([2017, 0, 1]),
      moment([2017, 1, 1]),  moment([2017, 2, 1]),  moment([2017, 3, 1])
    ];
    let startMonth = moment([2015, 4, 1]);
    let actual = service.get(startMonth);

    for (let i = 0; i < expected.length; i++) {
      expect(expected[i]).toBeSameAs(actual[i]);
    }
  }));
});
