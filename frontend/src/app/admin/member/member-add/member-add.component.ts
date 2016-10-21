import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, Validators, FormControl, FormGroup} from '@angular/forms';

import {MemberService, Member} from '../member.service';
import {CustomValidators} from '../../../utils/custom-validators';

@Component({
  selector: 'app-member-add',
  templateUrl: 'member-add.component.html',
  styleUrls: ['member-add.component.sass']
})
export class MemberAddComponent {

  private member: Member;

  memberForm: FormGroup;

  ctrlFirstName: FormControl;
  ctrlLastName: FormControl;
  ctrlPhoneNumber: FormControl;
  ctrlEmail: FormControl;
  ctrlLastJump: FormControl;
  ctrlWeight: FormControl;
  ctrlHeight: FormControl;
  ctrlDriver: FormControl;
  ctrlOrganiser: FormControl;

  /**
   * Set when an API request fails.
   *
   * @type {boolean}
   */
  apiRequestFailed: boolean = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber: boolean = false;

  /**
   * Any errors returned by the API that are not network or availability related.
   */
  error: string;

  constructor(private builder: FormBuilder,
              private service: MemberService,
              private router: Router) {
    this.ctrlFirstName = new FormControl('', Validators.required);
    this.ctrlLastName = new FormControl('');
    this.ctrlPhoneNumber = new FormControl('', CustomValidators.phoneNumber);
    this.ctrlEmail = new FormControl('', CustomValidators.email);
    this.ctrlLastJump = new FormControl('');
    this.ctrlWeight = new FormControl('');
    this.ctrlHeight = new FormControl('');
    this.ctrlDriver = new FormControl('');
    this.ctrlOrganiser = new FormControl('');

    this.memberForm = builder.group({
      firstName: this.ctrlFirstName,
      lastName: this.ctrlLastName,
      phoneNumber: this.ctrlPhoneNumber,
      email: this.ctrlEmail,
      lastJump: this.ctrlLastJump,
      weight: this.ctrlWeight,
      height: this.ctrlHeight,
      driver: this.ctrlDriver,
      organiser: this.ctrlOrganiser
    });
  }

  createMember() {
    this.showThrobber = true;

    this.service.addMember(this.memberForm.value).subscribe(
      result => {
        this.showThrobber = false;

        this.router.navigate(['/admin', 'members', result.uuid]);
      },
      error => {
        this.showThrobber = false;

        console.log(error);
        this.apiRequestFailed = true;
      }
    );
  }

}
