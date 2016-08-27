import { Component, OnInit } from '@angular/core';

import { PageNavComponent } from './index';

@Component({
  selector: 'app-base',
  templateUrl: 'base.component.html',
  styleUrls: ['base.component.sass'],
  directives: [PageNavComponent]
})
export class BaseComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
