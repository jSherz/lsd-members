import { Component, Input } from '@angular/core';
import { ROUTER_DIRECTIVES } from '@angular/router';

@Component({
  moduleId: module.id,
  selector: 'nav-item',
  templateUrl: 'nav.component.html',
  directives: [ROUTER_DIRECTIVES]
})

export class NavComponent {
  @Input() href: String;

  @Input() linkText: String;
}
