import * as moment from 'moment';

export const MOMENT_MATCHER = {
  toBeSameAs: (util, customEqualityTesters) => {
    return {
      compare: (actual: moment.Moment, expected: moment.Moment) => {
        const result: any = {};

        result.pass = actual.isSame(expected);

        if (result.pass) {
          result.message = `${expected} was the same as ${actual}`;
        } else {
          result.message = `${expected} was not the same as ${actual}`;
        }

        return result;
      }
    };
  }
};
