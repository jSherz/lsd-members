import { Subscription      } from 'rxjs';
import { ActivatedRoute    } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { HTTP_PROVIDERS    } from '@angular/http';
import * as moment        from 'moment';

import {
  CourseService,
  Course,
  CourseSpaceWithMember,
  CourseServiceImpl, CourseWithOrganisers, CommitteeMember
} from '../course.service';

@Component({
  selector: 'app-course-view',
  templateUrl: 'course-view.component.html',
  providers: [HTTP_PROVIDERS, { provide: CourseService, useClass: CourseServiceImpl }]
})
export class CourseViewComponent implements OnInit {

  /**
   * Subscribes to the current activated route and displays different courses as it changes.
   */
  private displayCourseSub: Subscription;

  /**
   * The currently displayed course (if any).
   */
  private course: CourseWithOrganisers = new CourseWithOrganisers(
    new Course("Loading", moment([1800, 0, 1]), "Loading", "Loading", 0),
    new CommitteeMember("Loading", "Loading"),
    new CommitteeMember("Loading", "Loading")
  );

  /**
   * The spaces on this course, if a course is loaded.
   */
  private spaces: CourseSpaceWithMember[] = [];

  /**
   * Set when an API request fails.
   *
   * @type {boolean}
   */
  apiRequestFailed: boolean = false;

  constructor(private route: ActivatedRoute, private service: CourseService) { }

  /**
   * Called when the component has been created.
   *
   * Subscribes to URL parameters to pick out the course UUID.
   */
  ngOnInit() {
    this.displayCourseSub = this.route.params
      .subscribe(params => {
        let uuid: string = params['uuid'];

        this.updateCourse(uuid);
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
    this.service.get(uuid).subscribe(
      course => {
        // Ensure date is a moment
        course.course.date = moment(course.course.date);

        this.course = course;
      },
      error => {
        this.apiRequestFailed = true;
        console.log('Failed to get course info:');
        console.log(error);
      }
    );

    this.service.spaces(uuid).subscribe(
      spaces => {
        this.spaces = spaces.map(space => {
          // Ensure createdAt is a moment
          if (space.member) {
            space.member.createdAt = moment(space.member.createdAt);
          }

          return space;
        });
      },
      error => {
        this.apiRequestFailed = true;
        console.log('Failed to get course spaces:');
        console.log(error);
      }
    );
  }

  /**
   * Turn a status returned by the API into a meaningful version.
   *
   * @param status
   */
  private translateStatus(status: number): string {
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
  private formatDate(input: moment.Moment) {
    if (input && input.format) {
      return input.format('dddd, MMMM Do YYYY')
    } else {
      return 'Unknown';
    }
  }

}
