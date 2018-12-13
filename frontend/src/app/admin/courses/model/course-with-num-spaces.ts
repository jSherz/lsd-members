import { Course } from "./";

export class CourseWithNumSpaces {
  course: Course;
  totalSpaces: number;
  spacesFree: number;

  constructor(course: Course, totalSpaces: number, spacesFree: number) {
    this.course = course;
    this.totalSpaces = totalSpaces;
    this.spacesFree = spacesFree;
  }
}
