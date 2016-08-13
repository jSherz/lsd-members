import {Component} from '@angular/core';
import {ROUTER_DIRECTIVES} from '@angular/router';

import {NavComponent} from '../utils';

@Component({
  selector: 'signup-base-component',
  templateUrl: 'signup-base.component.html',
  directives: [
    ROUTER_DIRECTIVES,
    NavComponent
  ]
})
export class SignupBaseComponent { }
