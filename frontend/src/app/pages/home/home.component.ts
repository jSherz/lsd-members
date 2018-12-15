import { Component, OnInit } from "@angular/core";

@Component({
  selector: "lsd-home",
  templateUrl: "home.component.html",
  styleUrls: ["home.component.sass"]
})
export class HomeComponent {
  showVideo = false;

  loadVideo() {
    this.showVideo = true;
  }
}
