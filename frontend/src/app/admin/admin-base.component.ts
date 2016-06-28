import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

import { NavComponent } from '../utils/nav.component';

@Component({
  moduleId: module.id,
  templateUrl: 'admin-base.component.html',
  styleUrls: ['/css/admin.css'],
  directives: [
    ROUTER_DIRECTIVES,
    NavComponent
  ]
})
export class AdminBaseComponent { }
