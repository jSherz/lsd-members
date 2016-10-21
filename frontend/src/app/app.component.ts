import {Component, ViewEncapsulation} from '@angular/core';
import './rxjs-operators';

@Component({
  selector: 'lsd-root',
  template: '<router-outlet></router-outlet>',
  styleUrls: ['../assets/css/application.sass'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
}
