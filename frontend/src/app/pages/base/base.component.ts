import { Component, ViewEncapsulation } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';

@Component({
  selector: 'lsd-base',
  templateUrl: 'base.component.html',
  styleUrls: ['base.component.sass'],
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
