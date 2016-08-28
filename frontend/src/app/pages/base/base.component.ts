import { Component, OnInit, ViewEncapsulation } from '@angular/core';

import { PageNavComponent } from './index';

@Component({
  selector: 'app-base',
  templateUrl: 'base.component.html',
  styleUrls: ['base.component.sass'],
  directives: [PageNavComponent],
  encapsulation: ViewEncapsulation.None
})
export class BaseComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
