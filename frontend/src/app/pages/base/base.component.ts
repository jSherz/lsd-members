import { Component, ViewEncapsulation } from '@angular/core';
import {Router, NavigationStart} from '@angular/router';

import { PageNavComponent } from './index';

@Component({
  selector: 'app-base',
  templateUrl: 'base.component.html',
  styleUrls: ['base.component.sass'],
  directives: [PageNavComponent],
  encapsulation: ViewEncapsulation.None
})
export class BaseComponent {

  menuCollapsed = true;

  constructor(private router: Router) {
    // Close menu when navigating
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.menuCollapsed = true;
      }
    });
  }

  toggleMenu() {
    this.menuCollapsed = this.menuCollapsed ? false : true;
  }

}
