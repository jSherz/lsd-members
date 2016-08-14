import {Component, OnInit, OnDestroy} from '@angular/core';
import {
  Router,
  ROUTER_DIRECTIVES,
  ActivatedRoute
} from '@angular/router';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators,
  REACTIVE_FORM_DIRECTIVES
} from '@angular/forms';
import * as moment from 'moment';
import { HTTP_PROVIDERS } from '@angular/http';
import { Subscription } from 'rxjs';

import {
  CommitteeService,
  CommitteeServiceImpl,
  StrippedCommitteeMember
} from '../../committee/committee.service';

@Component({
  selector: 'course-add-component',
  templateUrl: 'course-add.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES],
  providers: [
    HTTP_PROVIDERS,
    { provide: CommitteeService, useClass: CommitteeServiceImpl }
  ]
})
export class CourseAddComponent implements OnInit, OnDestroy {

  courseForm: FormGroup;

  ctrlDate: FormControl;
  ctrlOrganiser: FormControl;
  ctrlSecondaryOrganiser: FormControl;
  ctrlNumSpaces: FormControl;

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
   * Any errors returned by the API.
   */
  errors: {
    numSpaces: undefined
  };

  /**
   * A list of active committee members.
   */
  committeeMembers: StrippedCommitteeMember[];

  /**
   * Maximum number of spaces a user can create a course with.
   *
   * @type {number}
   */
  maxSpaces: number = 50;

  /**
   * Reference is kept so we can clean it up when this component is destroyed.
   */
  private committeeMembersSub: Subscription;

  /**
   * Reference is kept so we can clean it up when this component is destroyed.
   */
  private routeSub: Subscription;

  /**
   * Build the course add form, including setting up any validation.
   *
   * @param builder
   * @param router
   * @param service
   */
  constructor(private builder: FormBuilder, private router: Router, private service: CommitteeService,
              private route: ActivatedRoute) {
    this.ctrlDate = new FormControl('', Validators.required);
    this.ctrlOrganiser = new FormControl('', Validators.required);
    this.ctrlSecondaryOrganiser = new FormControl('');
    this.ctrlNumSpaces = new FormControl('8', Validators.required);

    this.courseForm = builder.group({
      date: this.ctrlDate,
      organiser: this.ctrlOrganiser,
      secondaryOrganiser: this.ctrlSecondaryOrganiser,
      numSpaces: this.ctrlNumSpaces
    });
  }

  ngOnInit() {
    this.committeeMembersSub = this.service.active().subscribe(
      committee => this.committeeMembers = committee,
      error => {
        this.apiRequestFailed = true;
        console.log('Failed to get committee members: ' + error);
      }
    );

    this.routeSub = this.route.params
      .subscribe(params => {
        let year: number = +params['year'];
        let month: number = +params['month'];
        let day: number = +params['day'];

        if (!isNaN(year) && !isNaN(month) && !isNaN(day)) {
          // Convert 1 indexed month to 0 indexed for momentjs / JS
          let zeroIndexedMonth: number = month - 1;
          this.ctrlDate.updateValue(moment([year, zeroIndexedMonth, day]).format('YYYY-MM-DD'));
        }
      });
  }

  ngOnDestroy(): any {
    this.committeeMembersSub.unsubscribe();
    this.routeSub.unsubscribe();
  }

}
