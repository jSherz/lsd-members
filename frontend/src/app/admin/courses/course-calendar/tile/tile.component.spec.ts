/* tslint:disable:no-unused-variable */

import {async} from '@angular/core/testing';

import * as moment from 'moment';
import {Tile} from './tile.service';
import {TileComponent} from './tile.component';

describe('Component: Tile', () => {

  let mock = (tile: Tile) => {
    let component = new TileComponent();
    component.tile = tile;

    return component;
  };

  it('selects the correct class for today\'s tile', async(() => {
    let comp = mock(new Tile(moment(), false, false, true));

    expect(comp.otherClasses()).toEqual('tile-today');
  }));

  it('selects the correct class for a day in the previous or next months', async(() => {
    let compPrevMonth = mock(new Tile(moment(), true, false, false));
    let compNextMonth = mock(new Tile(moment(), false, true, false));

    expect(compPrevMonth.otherClasses()).toEqual('tile-other-month');
    expect(compNextMonth.otherClasses()).toEqual('tile-other-month');
  }));

  it('does not have any additional class if not today and this month', async(() => {
    let comp = mock(new Tile(moment(), false, false, false));

    expect(comp.otherClasses()).toEqual('');
  }));

});
