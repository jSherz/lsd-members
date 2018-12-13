import { Input, Component } from "@angular/core";

@Component({
  selector: "lsd-nav-item",
  templateUrl: "./nav.component.html",
  styleUrls: ["nav.component.sass"]
})
export class NavComponent {
  @Input()
  href: String;

  @Input()
  linkText: String;
}
