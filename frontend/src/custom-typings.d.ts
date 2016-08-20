// Typings reference file, see links for more information
// https://github.com/typings/typings
// https://www.typescriptlang.org/docs/handbook/writing-declaration-files.html

import * as moment from 'moment';
declare var System: any;
declare var module: { id: string };
declare var require: any;

declare namespace jasmine {
  interface Matchers {
    toBeSameAs(expected: moment.Moment): boolean;
  }
}
