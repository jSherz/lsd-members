import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import * as moment from 'moment';

import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {StrippedCommitteeMember, CommitteeService} from '../../committee';

import {CourseService} from '../course.service';
import {CourseCreateRequest} from '../model';

@Component({
  selector: 'lsd-course-add-component',
  templateUrl: 'course-add.component.html',
  styleUrls: ['course-add.component.sass']
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
  apiRequestFailed = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber = false;

  /**
   * Any errors returned by the API.
   */
  errors: {
    numSpaces: string,
    general: string
  };

  /**
   * The most people that can be on a static line course.
   * @type {number}
   */
  maxSpaces = 15;

  /**
   * A list of active committee members.
   */
  committeeMembers: StrippedCommitteeMember[];

  /**
   * Reference is kept so we can clean it up when this component is destroyed.
   */
  private committeeMembersSub: Subscription;

  /**
   * Build the course add form, including setting up any validation.
   *
   * @param builder
   * @param service
   * @param committeeService
   * @param router
   */
  constructor(private builder: FormBuilder, private service: CourseService, private committeeService: CommitteeService,
              private router: Router) {
    this.ctrlDate = new FormControl(moment().format('YYYY-MM-DD'), Validators.required);
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
    this.committeeMembersSub = this.committeeService.active().subscribe(
      committee => this.committeeMembers = committee,
      error => {
        this.apiRequestFailed = true;
        console.log('Failed to get committee members: ' + error);
      }
    );
  }

  ngOnDestroy(): any {
    this.committeeMembersSub.unsubscribe();
  }

  createCourse(formData: any) {
    console.log('Showing throbber...');
    this.showThrobber = true;

    const request = new CourseCreateRequest(
      moment(formData.date),
      formData.organiser,
      formData.secondaryOrganiser === null || formData.secondaryOrganiser === '' ? null : formData.secondaryOrganiser,
      parseInt(formData.numSpaces, 10)
    );

    this.service.create(request).subscribe(
      result => {
        this.showThrobber = false;

        if (result.success) {
          this.router.navigate(['/admin', 'courses', result.uuid]);
        } else if (result.error === 'error.invalidNumSpaces') {
          this.errors = {
            numSpaces: 'error.invalidNumSpaces',
            general: null
          };
        } else {
          this.errors = {
            numSpaces: null,
            general: result.error
          };
        }
      },
      error => {
        this.showThrobber = false;
        this.apiRequestFailed = true;
        console.log('Failed to create course: ' + error);
      }
    );
  }

  translate(key: string): string {
    if ('error.invalidNumSpaces' === key) {
      return 'Invalid number of spaces - a course must have at least one space and at most 50.';
    } else {
      return key;
    }
  }

}
