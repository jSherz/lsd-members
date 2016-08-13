import {Component, ViewEncapsulation} from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { NavComponent } from './utils/nav.component';
import './rxjs-operators';

@Component({
  selector: 'app-root',
  template: '<router-outlet></router-outlet>',
  directives: [
    ROUTER_DIRECTIVES,
    NavComponent
  ],
  styleUrls: ['../assets/css/application.sass'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
  private siteTitle = 'Leeds University Skydivers';

  private titleService: Title = new Title();

  setTitle = function (newTitle: String) {
    if (newTitle !== undefined && newTitle !== '') {
      this.titleService.setTitle(`${newTitle} - ${this.siteTitle}`);
    } else {
      this.titleService.setTitle(this.siteTitle);
    }
  };
}
