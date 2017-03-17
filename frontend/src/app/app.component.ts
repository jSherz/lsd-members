import {Component, ViewEncapsulation} from '@angular/core';
import './rxjs-operators';

@Component({
  selector: 'lsd-root',
  template: '<router-outlet></router-outlet>',
  styles: [],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
}
