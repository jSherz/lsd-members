import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  moduleId: module.id,
  selector: 'app-thank-you',
  templateUrl: 'thank-you.component.html',
  styleUrls: ['thank-you.component.css']
})
export class ThankYouComponent {

  constructor(private location: Location) { }

  goBack() {
    this.location.back();
  }

}
