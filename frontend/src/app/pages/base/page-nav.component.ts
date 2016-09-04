import { Component, Input } from '@angular/core';

@Component({
  selector: 'page-nav-item',
  templateUrl: 'page-nav.component.html'
})

export class PageNavComponent {

  @Input() href: string;

  @Input() linkText: string;

  @Input() collapsed: boolean;

}
