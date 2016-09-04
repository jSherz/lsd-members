import { Input, Component } from '@angular/core';

@Component({
  selector: 'nav-item',
  templateUrl: './nav.component.html'
})

export class NavComponent {
  @Input() href: String;

  @Input() linkText: String;
}
