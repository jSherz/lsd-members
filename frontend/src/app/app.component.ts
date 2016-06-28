import { Component } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';
import { NavComponent } from './utils/nav.component'

@Component({
  moduleId: module.id,
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.css'],
  directives: [
    ROUTER_DIRECTIVES,
    NavComponent
  ]
})

export class AppComponent { }
