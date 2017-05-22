import {Component, Input, OnInit} from '@angular/core';
import {BasicInfo} from '../basic-info';

@Component({
  selector: 'lsd-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.sass']
})
export class ProfileComponent {

  @Input() basicInfo: BasicInfo;

  hasName() {
    return this.basicInfo && this.basicInfo.firstName;
  }

}
