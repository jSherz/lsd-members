import {Component, ViewEncapsulation} from '@angular/core';
import {Angulartics2GoogleAnalytics} from 'angulartics2/ga';

@Component({
  selector: 'lsd-root',
  template: '<router-outlet></router-outlet>',
  styles: [],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
  constructor(angulartics2GoogleAnalytics: Angulartics2GoogleAnalytics) {
  }
}
