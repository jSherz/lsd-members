import { Component, Input  } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

import { Tile } from './tile.service';

@Component({
  selector: 'calendar-tile',
  templateUrl: 'tile.component.html',
  directives: [ROUTER_DIRECTIVES]
})
export class TileComponent {

  @Input() tile: Tile;

  otherClasses(): string {
    if (this.tile.isToday) {
      return 'tile-today';
    } else if (this.tile.isPreviousMonth || this.tile.isNextMonth) {
      return 'tile-other-month';
    } else {
      return '';
    }
  }

}
