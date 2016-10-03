import {Course, CommitteeMember} from './';

export class CourseWithOrganisers {
  course: Course;
  organiser: CommitteeMember;
  secondaryOrganiser: CommitteeMember;

  constructor(course: Course, organiser: CommitteeMember, secondaryOrganiser: CommitteeMember) {
    this.course = course;
    this.organiser = organiser;
    this.secondaryOrganiser = secondaryOrganiser;
  }
}
