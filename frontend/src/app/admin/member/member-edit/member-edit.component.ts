import {Component, OnInit, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators, FormControl, FormGroup} from '@angular/forms';
import * as moment from 'moment';

import {Member} from '../model';
import {MemberService} from '../member.service';
import {CustomValidators} from '../../../utils/custom-validators';


@Component({
  selector: 'lsd-member-edit',
  templateUrl: 'member-edit.component.html',
  styleUrls: ['member-edit.component.sass']
})
export class MemberEditComponent implements OnInit, OnDestroy {

  private displayMemberSub: Subscription;

  member: Member;

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
  apiRequestFailed = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber = false;

  /**
   * Any errors returned by the API that are not network or availability related.
   */
  error: string;

  constructor(private builder: FormBuilder,
              private service: MemberService,
              private route: ActivatedRoute,
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

  showMember(uuid: string) {
    this.service.getMember(uuid).subscribe(member => {
      // TODO: Replace with setting form value
      this.ctrlFirstName.setValue(member.firstName);
      this.ctrlLastName.setValue(member.lastName);
      this.ctrlPhoneNumber.setValue(member.phoneNumber);
      this.ctrlEmail.setValue(member.email);
      this.ctrlLastJump.setValue(member.lastJump ? member.lastJump.format('YYYY-MM-DD') : null);
      this.ctrlWeight.setValue(member.weight);
      this.ctrlHeight.setValue(member.height);
      this.ctrlDriver.setValue(member.driver);
      this.ctrlOrganiser.setValue(member.organiser);

      this.member = member;
    });
  }

  updateMember() {
    this.showThrobber = true;

    const memberData = this.memberForm.value;
    memberData.uuid = this.member.uuid;
    memberData.createdAt = this.member.createdAt.format('YYYY-MM-DD hh:mm:ss');
    memberData.updatedAt = moment().format('YYYY-MM-DD hh:mm:ss');

    this.service.editMember(memberData).subscribe(
      result => {
        this.showThrobber = false;

        this.router.navigate(['/admin', 'members', this.member.uuid]);
      },
      error => {
        this.showThrobber = false;

        console.log(error);
        this.apiRequestFailed = true;
      }
    );
  }

  ngOnInit() {
    this.displayMemberSub = this.route.params
      .subscribe(params => {
        const uuid: string = params['uuid'];

        this.showMember(uuid);
      });
  }

  ngOnDestroy() {
    this.displayMemberSub.unsubscribe();
  }

}
