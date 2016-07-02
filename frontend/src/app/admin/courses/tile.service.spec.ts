/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import * as moment from 'moment';
import { MOMENT_MATCHERS } from './../utils/moment-matchers';

import { Tile, TileService } from './tile.service';

describe('Tile Service', () => {
  beforeEachProviders(() => [TileService]);

  var testSamples: Tile[][] = []
  var service = undefined

  beforeEach(() => {
    service = new TileService();

    testSamples = [
      service.getTiles(moment([2016, 5, 1]), moment([2016, 5, 20])),
      service.getTiles(moment([2017, 5, 1]), moment([2016, 5, 15])),
      service.getTiles(moment([2018, 5, 1]), moment([2016, 5, 28])),
      service.getTiles(moment([2016, 6, 1]), moment([2016, 6, 1])),
      service.getTiles(moment([2016, 7, 1]), moment([2016, 7, 3]))
    ];
  });

  it('should always return 42 tiles', () => {
    testSamples.map(x => expect(x.length).toEqual(42));
  });

  it('should return tiles in the correct date order', () => {
    testSamples.map((sample) => {
      var lastTile = sample[0];

      for (var i = 1; i < sample.length; i++) {
        expect(sample[i].date.isAfter(lastTile.date)).toEqual(true);
        expect(sample[i].date.clone().subtract(1, 'days').isSame(lastTile.date)).toEqual(true);

        lastTile = sample[i];
      }
    });
  });

  it('should start on the correct day of the previous month', () => {
    // Months starting on Monday -> Sunday with the number of days that should be
    // shown from the previous month.
    let monthsStartingOnDays: [moment.Moment, number][] = [
      [moment([2016, 4, 1]), 6], // Sunday (0)
      [moment([2016, 1, 1]), 7], // Monday (1)
      [moment([2016, 2, 1]), 1],
      [moment([2016, 5, 1]), 2],
      [moment([2016, 8, 1]), 3],
      [moment([2016, 0, 1]), 4],
      [moment([2016, 9, 1]), 5]  // Saturday (6)
    ];

    monthsStartingOnDays.map(([firstOfMonth, expectedNumDays]) => {
      let results: Tile[] = service.getTiles(firstOfMonth, firstOfMonth);
      let correctStartDate = firstOfMonth.clone().subtract(expectedNumDays, 'days');

      expect(results[0].date.isSame(correctStartDate)).toEqual(true);
    });
  });
});
