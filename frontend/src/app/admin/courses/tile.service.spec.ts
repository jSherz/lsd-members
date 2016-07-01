/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { TileService } from './tile.service';

describe('Tile Service', () => {
  beforeEachProviders(() => [TileService]);

  it('should ...',
      inject([TileService], (service: TileService) => {
    expect(service).toBeTruthy();
  }));
});
