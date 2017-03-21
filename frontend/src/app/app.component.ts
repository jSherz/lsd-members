import {Component, ViewEncapsulation} from '@angular/core';
import './rxjs-operators';
import {Angulartics2GoogleAnalytics} from "angulartics2";

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
