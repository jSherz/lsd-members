import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import * as moment from 'moment';

export const MOMENT_MATCHER: jasmine.CustomMatcherFactories = {
  toBeSameAs: (util, customEqualityTesters) => {
    return {
      compare: (actual: moment.Moment, expected: moment.Moment) => {
        let result: any = { };

        result.pass = actual.isSame(expected);

        if (result.pass) {
          result.message = `${expected} was the same as ${actual}`;
        } else {
          result.message `${expected} was not the same as ${actual}`;
        }

        return result;
      }
    };
  }
};
