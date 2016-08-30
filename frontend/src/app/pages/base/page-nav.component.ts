import { Component, Input } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

@Component({
  selector: 'page-nav-item',
  templateUrl: 'page-nav.component.html',
  directives: [ROUTER_DIRECTIVES]
})

export class PageNavComponent {

  @Input() href: string;

  @Input() linkText: string;

  @Input() collapsed: boolean;

}
