import {Component, OnInit, OnDestroy} from '@angular/core';
import {Router} from '@angular/router'
import {Subscription} from 'rxjs';
import * as moment from 'moment';

import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import {
  CommitteeService,
  StrippedCommitteeMember
} from '../../committee/committee.service';

import {CourseService} from '../course.service';
import {CourseCreateRequest} from '../model';

@Component({
  selector: 'course-add-component',
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
    let request = new CourseCreateRequest(
      moment(formData.date),
      formData.organiser,
      formData.secondaryOrganiser == null || formData.secondaryOrganiser == '' ? null : formData.secondaryOrganiser,
      parseInt(formData.numSpaces)
    );

    this.service.create(request).subscribe(
      result => {
        if (result.success) {
          this.router.navigate(['/admin', 'courses', result.uuid]);
        } else {
          this.errors['general'] = result.error;
        }
      },
      error => {
        this.apiRequestFailed = true;
        console.log('Failed to create course: ' + error);
      }
    );
  }

}
