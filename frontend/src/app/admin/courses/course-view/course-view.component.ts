import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {Component, OnInit, OnDestroy} from '@angular/core';
import * as moment from 'moment';

import {CourseService} from '../course.service';

import {
  StrippedMember,
  CourseSpaceWithMember,
  CourseWithOrganisers
} from '../model';

import {CourseSpaceService} from '../../course-spaces/course-spaces.service';


@Component({
  selector: 'lsd-course-view',
  templateUrl: 'course-view.component.html',
  styleUrls: ['course-view.component.sass']
})
export class CourseViewComponent implements OnInit, OnDestroy {

  /**
   * Subscribes to the current activated route and displays different courses as it changes.
   */
  private displayCourseSub: Subscription;

  /**
   * The currently displayed course (if any).
   */
  course: CourseWithOrganisers = null;

  /**
   * The spaces on this course, if a course is loaded.
   */
  spaces: CourseSpaceWithMember[] = [];

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
  showThrobber = true;

  /**
   * The course that is being displayed (if loaded).
   *
   * @type {any}
   */
  currentCourseUuid: string = null;

  constructor(private route: ActivatedRoute,
              private service: CourseService,
              private spaceService: CourseSpaceService) {
  }

  /**
   * Called when the component has been created.
   *
   * Subscribes to URL parameters to pick out the course UUID.
   */
  ngOnInit() {
    this.displayCourseSub = this.route.params
      .subscribe(params => {
        console.log('Loading course ' + params['uuid']);
        const uuid: string = params['uuid'];

        this.updateCourse(uuid);
        this.updateSpaces(uuid);
      });
  }

  /**
   * Clear the activated route subscription.
   */
  ngOnDestroy() {
    this.displayCourseSub.unsubscribe();
  }

  /**
   * Retrieve information about the currently selected course ready to be rendered on the page.
   */
  private updateCourse(uuid: string) {
    this.currentCourseUuid = uuid;

    this.service.getByUuid(uuid).subscribe(
      course => {
        if (this.spaces.length >= 1) {
          this.showThrobber = false;
        }

        // Ensure date is a moment
        course.course.date = moment(course.course.date);

        this.course = course;
      },
      error => {
        this.apiRequestFailed = true;
        this.showThrobber = false;

        console.error('Failed to get course info:');
        console.error(error);
      }
    );
  }

  /**
   * Load the list of spaces on this course from the API.
   */
  private updateSpaces(uuid: string) {
    this.service.spaces(uuid).subscribe(
      spaces => {
        if (this.course !== null) {
          this.showThrobber = false;
        }

        this.spaces = spaces.map(space => {
          // Rebuild member to ensure it has the correct instance methods
          if (space.member) {
            space.member = new StrippedMember(
              space.member.firstName,
              space.member.lastName,
              space.member.uuid,
              space.member.weight,
              space.member.height,
              moment(space.member.createdAt)
            );
          }

          return space;
        });
      },
      error => {
        this.apiRequestFailed = true;
        this.showThrobber = false;

        console.error('Failed to get course spaces:');
        console.error(error);
      }
    );
  }

  /**
   * Add the specified member to the next free space on the course.
   *
   * Called by the MemberSearch component.
   *
   * @param event From search component
   */
  addMemberToCourse(event) {
    const nextSpace = this.spaces.filter(space => space.member == null)[0];

    if (nextSpace) {
      this.spaceService.addMember(nextSpace.uuid, event.uuid).subscribe(
        result => {
          if (result.success) {
            this.updateSpaces(this.currentCourseUuid);
          } else {
            alert(this.translateError(result.error));
          }
        },
        error => {
          this.apiRequestFailed = true;
          this.showThrobber = false;

          console.error('Failed to add member to course:');
          console.error(error);
        }
      );
    } else {
      console.error('Unable to add member to this course - no spaces available.');
    }
  }

  /**
   * Remove the selected member from the course.
   *
   * @param space
   */
  removeMemberFromCourse(space: CourseSpaceWithMember) {
    this.spaceService.removeMember(space.uuid, space.member.uuid).subscribe(
      result => {
        if (result.success) {
          this.updateSpaces(this.currentCourseUuid);
        } else {
          alert(this.translateError(result.error));
        }
      },
      error => {
        this.apiRequestFailed = true;
        console.error('Failed to remove member from course:');
        console.error(error);
      }
    );
  }

  /**
   * Set the space's deposit to be paid or not paid.
   *
   * @param spaceUuid
   * @param depositPaid
   */
  setDepositPaid(spaceUuid: string, depositPaid: boolean) {
    this.spaceService.setDepositPaid(spaceUuid, depositPaid).subscribe(
      result => {
        this.updateSpaces(this.currentCourseUuid);
      },
      error => {
        this.apiRequestFailed = true;
        console.error('Failed to set deposit paid on course space ' + spaceUuid);
        console.error(error);
      }
    );
  }

  /**
   * Turn a status returned by the API into a meaningful version.
   *
   * @param status
   */
  translateStatus(status: number): string {
    switch (status) {
      case 0:
        return 'Pending';
      case 1:
        return 'Booked';
      default:
        return 'Unknown';
    }
  }

  /**
   * Pretty print a date.
   *
   * @param input
   * @returns {any}
   */
  formatDate(input: moment.Moment) {
    if (input && input.format) {
      return input.format('dddd, MMMM Do YYYY');
    } else {
      return 'Unknown';
    }
  }

  /**
   * Turn an API error into a human friendly version.
   *
   * @param key
   */
  private translateError(key: string): string {
    switch (key) {
      case 'error.alreadyOnCourse':
        return 'The selected member is already on this course.';
      case 'error.spaceNotEmpty':
        return 'A member is already on that course.';
      default:
        return 'An unknown error occurred.';
    }
  }

}
