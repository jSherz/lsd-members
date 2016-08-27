import { Component, Input } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

@Component({
  selector: 'page-nav-item',
  templateUrl: 'page-nav.component.html',
  styleUrls: ['page-nav.component.sass'],
  directives: [ROUTER_DIRECTIVES]
})

export class PageNavComponent {
  @Input() href: String;

  @Input() linkText: String;
}
