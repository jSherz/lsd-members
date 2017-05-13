import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'lsd-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.sass']
})
export class ProfileComponent {

  @Input() name: String;

  hasName() {
    return this.name && this.name.length >= 1;
  }

}
