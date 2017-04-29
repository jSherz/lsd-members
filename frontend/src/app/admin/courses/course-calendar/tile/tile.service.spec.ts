/* tslint:disable:no-unused-variable */

import * as moment from 'moment';

import {Tile, TileService} from './tile.service';
import {MOMENT_MATCHER} from '../../../../utils/moment-matcher';
import {async} from '@angular/core/testing';

describe('Tile Service', () => {

  const service: TileService = new TileService();

  const testSamples: Tile[][] = [
    service.getTiles(moment([2016, 5, 1]), moment([2016, 5, 20])),
    service.getTiles(moment([2017, 5, 1]), moment([2016, 5, 15])),
    service.getTiles(moment([2018, 5, 1]), moment([2016, 5, 28])),
    service.getTiles(moment([2016, 6, 1]), moment([2016, 6, 1])),
    service.getTiles(moment([2016, 7, 1]), moment([2016, 7, 3]))
  ];

  beforeEach(() => {
    jasmine.addMatchers(MOMENT_MATCHER);
  });

  it('should always return 42 tiles', async(() => {
    testSamples.map(x => expect(x.length).toEqual(42));
  }));

  it('should return tiles in the correct date order', async(() => {
    testSamples.map((sample) => {
      let lastTile = sample[0];

      for (let i = 1; i < sample.length; i++) {
        expect(sample[i].date.isAfter(lastTile.date)).toEqual(true);
        expect(sample[i].date.clone().subtract(1, 'days')).toBeSameAs(lastTile.date);

        lastTile = sample[i];
      }
    });
  }));

  it('should start on the correct day of the previous month', async(() => {
    // Months starting on Monday -> Sunday with the number of days that should be
    // shown from the previous month.
    const monthsStartingOnDays: [moment.Moment, number][] = [
      [moment([2016, 4, 1]), 6], // Sunday (0)
      [moment([2016, 1, 1]), 7], // Monday (1)
      [moment([2016, 2, 1]), 1],
      [moment([2016, 5, 1]), 2],
      [moment([2016, 8, 1]), 3],
      [moment([2016, 0, 1]), 4],
      [moment([2016, 9, 1]), 5]  // Saturday (6)
    ];

    monthsStartingOnDays.map(([firstOfMonth, expectedNumDays]) => {
      const results: Tile[] = service.getTiles(firstOfMonth, firstOfMonth);
      const correctStartDate = firstOfMonth.clone().subtract(expectedNumDays, 'days');

      expect(results[0].date).toBeSameAs(correctStartDate);
    });
  }));

  it('should highlight the current day correctly', async(() => {
    // A day in a month and the index (of generated tiles) we'd expect to be the
    // current today (_.isToday == true)
    const examples: [moment.Moment, number][] = [
      [moment([2016, 5, 15]), 16],
      [moment([2016, 6, 1]), 4],
      [moment([2016, 7, 18]), 24]
    ];

    examples.map(([dayInMonth, expectedTodayIndex]) => {
      const startOfMonth = moment([dayInMonth.year(), dayInMonth.month(), 1]);
      const tiles = service.getTiles(startOfMonth, dayInMonth);

      for (let i = 0; i < tiles.length; i++) {
        if (i === expectedTodayIndex) {
          expect(tiles[i].isToday).toEqual(true);
        } else {
          expect(tiles[i].isToday).toEqual(false);
        }
      }
    });
  }));

});
