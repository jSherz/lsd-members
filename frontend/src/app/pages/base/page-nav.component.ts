import { Component, Input } from "@angular/core";

@Component({
  selector: "lsd-page-nav-item",
  templateUrl: "page-nav.component.html"
})
export class PageNavComponent {
  @Input()
  preSeparator: Boolean = true;

  @Input()
  postSeparator: Boolean = false;

  @Input()
  href: string;

  @Input()
  linkText: string;

  @Input()
  collapsed: boolean;
}
