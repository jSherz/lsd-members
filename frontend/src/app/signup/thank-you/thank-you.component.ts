import { Component } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-thank-you',
  templateUrl: 'thank-you.component.html'
})
export class ThankYouComponent {

  constructor(private location: Location) { }

  goBack() {
    this.location.back();
  }

}
